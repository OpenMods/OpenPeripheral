package openperipheral.common.peripheral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;

import openperipheral.OpenPeripheral;
import openperipheral.api.IAttachable;
import openperipheral.api.IMethodDefinition;
import openperipheral.api.IRestriction;
import openperipheral.common.converter.TypeConversionRegistry;
import openperipheral.common.core.TickHandler;
import openperipheral.common.postchange.PostChangeRegistry;
import openperipheral.common.util.StringUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;

public abstract class AbstractPeripheral implements IHostedPeripheral {

	static class MySecurityManager extends SecurityManager {
		public String getCallerClassName(int callStackDepth) {
			return getClassContext()[callStackDepth].getName();
		}
	}

	private final static MySecurityManager mySecurityManager = new MySecurityManager();

	protected String name = "peripheral";
	protected ArrayList<IMethodDefinition> methods;
	protected String[] methodNames;

	public AbstractPeripheral() {
		
	}
	
	public abstract ArrayList<IMethodDefinition> getMethods();
	
	private ArrayList<IMethodDefinition> getMethodsCached() {
		if (methods == null) {
			methods = getMethods();
		}
		return methods;
	}

	@Override
	public String getType() {
		return name;
	}

	@Override
	public String[] getMethodNames() {
		if (methodNames == null) {
			ArrayList<String> mNames = new ArrayList<String>();
			mNames.add("listMethods");
			for (IMethodDefinition method : getMethodsCached()) {
				mNames.add(method.getLuaName());
			}
			methodNames = mNames.toArray(new String[mNames.size()]);
		}
		return methodNames;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, int methodId, Object[] arguments) throws Exception {

		if (methodId == 0) {
			return new Object[] { StringUtils.join(getMethodNames(), "\n") };
		}

		methodId--;

		String callerClass = mySecurityManager.getCallerClassName(2);

		boolean isCableCall = callerClass.equals("dan200.computer.shared.TileEntityCable$RemotePeripheralWrapper")
				|| callerClass.equals("openperipheral.common.tileentity.TileEntityProxy");

		
		final IMethodDefinition methodDefinition = getMethodsCached().get(methodId);
		
		if (methodDefinition != null) {

			if (!methodDefinition.needsSanitize()) {
				return executeMethod(isCableCall || methodDefinition.isInstant(), methodDefinition, arguments);
			}

			ArrayList<Object> args = new ArrayList(Arrays.asList(arguments));

			Class[] requiredParameters = methodDefinition.getRequiredParameters();

			if (requiredParameters != null) {

				replaceArguments(args, methodDefinition.getReplacements());

				if (args.size() != requiredParameters.length) {
					int replacements = 0;
					if (methodDefinition.getReplacements() != null) {
						replacements = methodDefinition.getReplacements().size();
					}
					throw new Exception("Invalid number of parameters. Expected " + (requiredParameters.length - replacements));
				}

				for (int i = 0; i < requiredParameters.length; i++) {
					Object converted = TypeConversionRegistry.fromLua(args.get(i), requiredParameters[i]);
					if (converted == null) {
						throw new Exception("Invalid parameter number " + (i + 1));
					}
					args.set(i, converted);
				}
			}

			for (int i = 0; i < args.size(); i++) {
				ArrayList<IRestriction> restrictions = methodDefinition.getRestrictions(i);
				if (restrictions != null) {
					for (IRestriction restriction : restrictions) {
						if (!restriction.isValid(args.get(i))) {
							throw new Exception(restriction.getErrorMessage(i + 1));
						}
					}
				}
			}

			final Object[] argsToUse = args.toArray(new Object[args.size()]);

			return executeMethod(isCableCall || methodDefinition.isInstant(), methodDefinition, argsToUse);
		}

		return null;

	}

	private Object[] executeMethod(boolean isInstant, final IMethodDefinition methodDefinition, final Object[] argsToUse) throws Exception {
		final Object target = getTargetObject();
		if (isInstant) {
			Object response = TypeConversionRegistry.toLua(methodDefinition.execute(target, argsToUse));
			PostChangeRegistry.onPostChange(target, methodDefinition, argsToUse);
			return new Object[] { response };
		} else {
			Future callback = TickHandler.addTickCallback(getWorldObject(), new Callable() {
				@Override
				public Object call() throws Exception {
					Object response = TypeConversionRegistry.toLua(methodDefinition.execute(target, argsToUse));
					PostChangeRegistry.onPostChange(target, methodDefinition, argsToUse);
					return response;
				}
			});
			return new Object[] { callback.get() };
		}
	}

	protected abstract void replaceArguments(ArrayList<Object> args, HashMap<Integer, String> replacements);

	@Override
	public boolean canAttachToSide(int side) {
		return true;
	}

	@Override
	public void attach(final IComputerAccess computer) {
		ModContainer container = FMLCommonHandler.instance().findContainerFor(OpenPeripheral.instance);
		try {
			computer.unmount("openp");
		} catch (Exception e) {
		}
		computer.mountFixedDir("openp", String.format("openperipheral/lua", container.getVersion()), true, 0);
		try {
			TickHandler.addTickCallback(getWorldObject(), new Callable() {
				@Override
				public Object call() throws Exception {
					Object target = getTargetObject();
					if (target != null && target instanceof IAttachable) {
						((IAttachable) target).addComputer(computer);
					}
					return null;
				}
			});
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void detach(final IComputerAccess computer) {

		try {
			TickHandler.addTickCallback(getWorldObject(), new Callable() {
				@Override
				public Object call() throws Exception {
					Object target = getTargetObject();
					if (target != null && target instanceof IAttachable) {
						((IAttachable) target).removeComputer(computer);
					}
					return null;
				}
			});
		} catch (InterruptedException e) {
		}

	}

	@Override
	public void update() {
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
	}

	public abstract Object getTargetObject();

	public abstract World getWorldObject();

}

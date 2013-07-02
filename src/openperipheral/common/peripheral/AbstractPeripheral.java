package openperipheral.common.peripheral;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;

import openperipheral.OpenPeripheral;
import openperipheral.api.IRestriction;
import openperipheral.common.converter.TypeConversionRegistry;
import openperipheral.common.core.TickHandler;
import openperipheral.common.interfaces.IAttachable;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
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
	protected ArrayList<IPeripheralMethodDefinition> methods;
	protected String[] methodNames;

	public AbstractPeripheral() {
		
	}
	
	public abstract ArrayList<IPeripheralMethodDefinition> getMethods();
	
	private ArrayList<IPeripheralMethodDefinition> getMethodsCached() {
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
			for (IPeripheralMethodDefinition method : getMethodsCached()) {
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

		
		final IPeripheralMethodDefinition methodDefinition = getMethodsCached().get(methodId);
		
		if (methodDefinition != null) {

			ArrayList<Object> args = new ArrayList(Arrays.asList(arguments));
			
			if (!methodDefinition.needsSanitize()) {
				return executeMethod(isCableCall || methodDefinition.isInstant(), methodDefinition, args);
			}

			Class[] requiredParameters = getRequiredParameters(methodDefinition);
			
			if (requiredParameters != null) {

				checkParameterCount(args, methodDefinition.getRequiredParameters());
				
				replaceArguments(args, methodDefinition.getReplacements());
				
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

			return executeMethod(isCableCall || methodDefinition.isInstant(), methodDefinition, args);
		}

		return null;

	}	
	
	protected void checkParameterCount(ArrayList<Object> args, Class[] requiredParameters) throws Exception {
		if (args.size() != requiredParameters.length) {
			throw new Exception("Invalid number of parameters.");
		}
	}

	private Object[] executeMethod(boolean isInstant, final IPeripheralMethodDefinition methodDefinition, ArrayList args) throws Exception {
		final Object target = getTargetObject(args, methodDefinition);
		preExecute(methodDefinition, args);
		final Object[] argsToUse = args.toArray(new Object[args.size()]);
		if (isInstant) {
			Object response = null;
			try {
				response = methodDefinition.execute(target, argsToUse);
			}catch (InvocationTargetException ex) {
				Throwable cause = ex.getCause();
				throw new Exception(cause.getMessage());
			}
			response = TypeConversionRegistry.toLua(response);
			PostChangeRegistry.onPostChange(target, methodDefinition, argsToUse);
			return new Object[] { response };
		} else {
			Future callback = TickHandler.addTickCallback(getWorldObject(), new Callable() {
				@Override
				public Object call() throws Exception {
					Object response = null;
					try {
						response = methodDefinition.execute(target, argsToUse);
					} catch(InvocationTargetException ex) {
						Throwable cause = ex.getCause();
						throw new Exception(cause.getMessage());
					}
					response = TypeConversionRegistry.toLua(response);
					PostChangeRegistry.onPostChange(target, methodDefinition, argsToUse);
					return response;
				}
			});
			return new Object[] { callback.get() };
		}
	}

	protected abstract void replaceArguments(ArrayList<Object> args, HashMap<Integer, String> replacements);

	public void preExecute(IPeripheralMethodDefinition method, ArrayList args) {
	}
	
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
					IAttachable target = getAttachable();
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
					Object target = getAttachable();
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

	public void addArguments(ArrayList<Object> args) {
		
	}
	
	public Class[] getRequiredParameters(IPeripheralMethodDefinition methodDefinition) {
		return methodDefinition.getRequiredParameters();
	}

	public abstract Object getTargetObject(ArrayList args, IPeripheralMethodDefinition luaMethod) throws Exception;
	public abstract World getWorldObject();
	protected IAttachable getAttachable() {
		return null;
	}

}

package openperipheral.core.peripheral;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openperipheral.OpenPeripheral;
import openperipheral.api.IMultiReturn;
import openperipheral.api.IRestriction;
import openperipheral.core.MethodDeclaration;
import openperipheral.core.TickHandler;
import openperipheral.core.converter.TypeConversionRegistry;
import openperipheral.core.interfaces.IAttachable;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.postchange.PostChangeRegistry;
import openperipheral.core.util.StringUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;

public abstract class AbstractPeripheral implements IHostedPeripheral {

	protected String name = "peripheral";
	protected ArrayList<MethodDeclaration> methods;
	protected String[] methodNames;

	public AbstractPeripheral() {
		
	}
	
	private ArrayList<MethodDeclaration> getMethodsCached() {
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
			for (MethodDeclaration method : getMethodsCached()) {
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

		
		final MethodDeclaration methodDefinition = getMethodsCached().get(methodId);
		
		if (methodDefinition != null) {

			ArrayList<Object> args = new ArrayList(Arrays.asList(arguments));

			Class[] requiredParameters = methodDefinition.getRequiredParameters();
			
			if (requiredParameters != null) {

				checkParameterCount(args, methodDefinition.getRequiredParameters());
				
				for (int i = 0; i < requiredParameters.length; i++) {
					Object converted = TypeConversionRegistry.fromLua(args.get(i), requiredParameters[i]);
					if (converted == null) {
						throw new Exception("Invalid parameter number " + (i + 1));
					}
					args.set(i, converted);
				}
				

				
			}

			return executeMethod(isCableCall || !methodDefinition.onTick(), methodDefinition, args);
		}

		return null;

	}	
	
	protected void checkParameterCount(ArrayList<Object> args, Class[] requiredParameters) throws Exception {
		if (args.size() != requiredParameters.length) {
			throw new Exception("Invalid number of parameters.");
		}
	}

	private Object[] executeMethod(boolean isInstant, final MethodDeclaration methodDefinition, ArrayList args) throws Exception {
		
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

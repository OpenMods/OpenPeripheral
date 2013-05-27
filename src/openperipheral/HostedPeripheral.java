package openperipheral;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;

public class HostedPeripheral implements IHostedPeripheral {

	static class MySecurityManager extends SecurityManager {
        public String getCallerClassName(int callStackDepth) {
            return getClassContext()[callStackDepth].getName();
        }
    }

    private final static MySecurityManager mySecurityManager =
        new MySecurityManager();
	
	private TileEntity tile;
	private ArrayList<MethodDefinition> definitions;
	private String[] methodNames;
	
	public HostedPeripheral(TileEntity tile) {
		this.tile = tile;
		
		definitions = OpenPeripheral.getMethodsForClass(tile.getClass());
		
		methodNames = new String[definitions.size()];
		int i = 0;
		for (MethodDefinition method : definitions) {
			methodNames[i++] = method.getName();
		}
	}
	
	@Override
	public String getType() {
		String t = tile.getBlockType().getUnlocalizedName();
		int i = t.lastIndexOf('.');
		if (i > 0) {
		    t = t.substring(i+1);
		}
		return t;
	}

	@Override
	public String[] getMethodNames() {
		return methodNames;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, int methodId,
			final Object[] arguments) throws Exception {

		boolean isCableCall = mySecurityManager.getCallerClassName(2) == "dan200.computer.shared.TileEntityCable$RemotePeripheralWrapper";
		
		ArrayList<Object> args = new ArrayList(Arrays.asList(arguments));
		
		MethodDefinition definition = definitions.get(methodId);

		if (!definition.isMethod()) {
			
			return getOrSetProperty(computer, methodId, definition, args, isCableCall);
			
		}
		
		final Method method = definition.getMethod();
		
		Class[] requiredParameters = method.getParameterTypes();

		for (Entry<Integer, String> entry : definition.getReplacements().entrySet()) {
			int index = entry.getKey();
			String val = String.valueOf(entry.getValue());
			args.add(index, OpenPeripheral.replacements.get(val).replace(tile));
		}
		
		if (args.size() != requiredParameters.length) {
			throw new Exception("Invalid number of parameters.");
		}
		
		fixArguments(requiredParameters, args);
		
		final Object[] argsToUse = args.toArray(new Object[args.size()]);
		
		if (!isCableCall) {
			Future callback = TickHandler.addTickCallback(
					tile.worldObj, new Callable() {
						@Override
						public Object call() throws Exception {
							if (method.getReturnType() == void.class) {
								method.invoke(tile, argsToUse);
								return true;
							}else {
								return TypeUtils.convertToSuitableType(method.invoke(tile, argsToUse));
							}
						}
					});
	
			return new Object[] { callback.get() };
		}else {
			if (method.getReturnType() == void.class) {
				method.invoke(tile, argsToUse);
				return new Object[] { true };
			}else {
				return new Object[] { TypeUtils.convertToSuitableType(method.invoke(tile, argsToUse)) };
			}
		}
	}

	private void fixArguments(Class[] requiredParameters, ArrayList<Object> args) throws Exception {
		int offset = 0;
		for (Class requiredParameter : requiredParameters) {
			
			Object argumentToCheck = args.get(offset);
			
			if (requiredParameter == ForgeDirection.class && argumentToCheck instanceof String) {
				
				args.set(offset, TypeUtils.stringToDirection((String)argumentToCheck));
				
			} else if (requiredParameter == ItemStack.class && argumentToCheck instanceof Map) {
				
				args.set(offset, TypeUtils.mapToItemStack((Map)argumentToCheck));
				
			}else if (requiredParameter == int.class && argumentToCheck instanceof Double){
			
				args.set(offset, (int)(double)(Double)argumentToCheck);
			
			}else if (requiredParameter == int.class && argumentToCheck instanceof Integer) {

				args.set(offset, (int)(Integer)argumentToCheck);
				
			}else if (!requiredParameter.isAssignableFrom(argumentToCheck.getClass())) {
				throw new Exception("Invalid parameter types");
			}
			
			offset++;
		}
	}

	private Object[] getOrSetProperty(IComputerAccess computer, int methodId,
			MethodDefinition definition, ArrayList<Object> args, boolean isCableCall) throws Exception {
		
		final Field field = definition.getField();
		
		if (args.size() != (definition.isGet() ? 0 : 1)) {
			throw new Exception("Invalid number of parameters.");
		}
		
		Class required = field.getType();

		if (isCableCall) {
			if (!definition.isGet()) {
				fixArguments(new Class[] { required }, args);
				final Object arg = args.get(0);
				field.set(tile, arg);
				return new Object[] { true };
			}else {
				return new Object[] { TypeUtils.convertToSuitableType(field.get(tile)) };
			}
			
		}else {
			if (!definition.isGet()) {
				fixArguments(new Class[] { required }, args);
				final Object arg = args.get(0);
				Future callback = TickHandler.addTickCallback(
						tile.worldObj, new Callable() {
							@Override
							public Object call() throws Exception {
								field.set(tile, arg);
								return true;
							}
						});
	
				return new Object[] { callback.get() };
			}else {
				Future callback = TickHandler.addTickCallback(
						tile.worldObj, new Callable() {
							@Override
							public Object call() throws Exception {
								return TypeUtils.convertToSuitableType(field.get(tile));
							}
						});
	
				return new Object[] { callback.get() };
			}
		}
	}

	@Override
	public boolean canAttachToSide(int side) {
		return true;
	}

	@Override
	public void attach(IComputerAccess computer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void detach(IComputerAccess computer) {
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

}

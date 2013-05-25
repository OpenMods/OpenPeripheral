package openperipheral;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;

public class HostedPeripheral implements IHostedPeripheral {

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
		return tile.getBlockType().getUnlocalizedName();
	}

	@Override
	public String[] getMethodNames() {
		return methodNames;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, int methodId,
			final Object[] arguments) throws Exception {
		
		ArrayList<Object> args = new ArrayList(Arrays.asList(arguments));
		
		MethodDefinition definition = definitions.get(methodId);
		final Method method = definition.getMethod();
		
		Class[] requiredParameters = method.getParameterTypes();

		for (Entry<Integer, String> entry : definition.getReplacements().entrySet()) {
			int index = entry.getKey();
			String val = entry.getValue();
			if (val.equals("world")) {
				args.add(index, tile.worldObj);
			}else if (val.equals("x")) {
				args.add(index, tile.xCoord);
			}else if (val.equals("y")) {
				args.add(index, tile.yCoord);
			}else if (val.equals("z")) {
				args.add(index, tile.zCoord);
			}
		}
		
		if (args.size() != requiredParameters.length) {
			throw new Exception("Invalid number of parameters.");
		}
		
		int offset = 0;
		for (Class requiredParameter : requiredParameters) {
			Object argumentToCheck = args.get(offset);
			if (requiredParameter == int.class && argumentToCheck instanceof Double){
				args.set(offset, (int)(double)(Double)argumentToCheck);
			
			}else if (requiredParameter == int.class && argumentToCheck instanceof Integer) {
				args.set(offset, (int)(Integer)argumentToCheck);
			
			} else if (!requiredParameter.isAssignableFrom(argumentToCheck.getClass())) {
			
				throw new Exception("Invalid parameter types");
			}
			offset++;
		}
		
		final Object[] argsToUse = args.toArray(new Object[args.size()]);
		Future callback = TickHandler.addTickCallback(
				tile.worldObj, new Callable() {
					@Override
					public Object call() throws Exception {
						return method.invoke(tile, argsToUse);
					}
				});

		return new Object[] { callback.get() };
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

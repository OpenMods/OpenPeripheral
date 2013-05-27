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

import openperipheral.definition.DefinitionMethod;
import openperipheral.definition.DefinitionMethod.CallType;

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

    private final static MySecurityManager mySecurityManager = new MySecurityManager();
	
	private World worldObj;
	private Class klass;
	private int x;
	private int y;
	private int z;
	private String name;
	private ArrayList<DefinitionMethod> methods;
	private String[] methodNames;
	
	public HostedPeripheral(TileEntity tile) {
		
		klass = tile.getClass();
		worldObj = tile.worldObj;
		x = tile.xCoord;
		y = tile.yCoord;
		z = tile.zCoord;
		methods = OpenPeripheral.getMethodsForClass(klass);
		ArrayList<String> mNames = new ArrayList<String>();
		for (DefinitionMethod method : methods) {
			mNames.add(method.getLuaName());
		}
		methodNames = mNames.toArray(new String[mNames.size()]);
		
		/* get the name */
		name = tile.getBlockType().getUnlocalizedName();
		int i = name.lastIndexOf('.');
		if (i > 0) {
			name = name.substring(i + 1);
		}
	}
	
	@Override
	public String getType() {
		return name;
	}

	@Override
	public String[] getMethodNames() {
		return methodNames;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, int methodId,
			Object[] arguments) throws Exception {
		
		boolean isCableCall = mySecurityManager.getCallerClassName(2) == "dan200.computer.shared.TileEntityCable$RemotePeripheralWrapper";
		
		final DefinitionMethod methodDefinition = methods.get(methodId);
		
		if (methodDefinition != null) {
			
			boolean isCallable = true;
			
			ArrayList<Object> args = new ArrayList(Arrays.asList(arguments));

			Class[] requiredParameters = methodDefinition.getRequiredParameters();
			
			if (args.size() != requiredParameters.length){
				throw new Exception("Invalid number of parameters. Expected " + requiredParameters.length);
			}
			
			for (int i = 0; i < requiredParameters.length; i++) {
				args.set(i, TypeConversionRegistry.fromLua(args.get(i), requiredParameters[i]));
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
			
			final TileEntity tile = worldObj.getBlockTileEntity(x, y, z);

			final Object[] argsToUse = args.toArray(new Object[args.size()]);
			
			if (isCableCall) {
				return new Object[] { 
						TypeConversionRegistry.toLua(methodDefinition.execute(tile, argsToUse))
				};
			}else {
				Future callback = TickHandler.addTickCallback(
						tile.worldObj, new Callable() {
							@Override
							public Object call() throws Exception {
								return TypeConversionRegistry.toLua(
										TypeConversionRegistry.toLua(methodDefinition.execute(tile, argsToUse))
								);
							}
						});

				return new Object[] { callback.get() };
			}
		}
		return null;
	}

	@Override
	public boolean canAttachToSide(int side) {
		return true;
	}

	@Override
	public void attach(IComputerAccess computer) {
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

package openperipheral.common.tileentity;

import openperipheral.common.block.BlockProxy;
import openperipheral.common.util.ReflectionHelper;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityProxy extends TileEntity implements IPeripheral {
	
	private IPeripheral peripheral;
	
	private boolean initialized = false;
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!initialized) {
			BlockProxy.refreshProxiedPeripheral(worldObj, xCoord, yCoord, zCoord);
			initialized = true;
		}
	}
	
	public void setPeripheral(IPeripheral peripheral) {

		this.peripheral = peripheral;
		
		if (!worldObj.isRemote){
			
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						
						int offsetX = xCoord + x;
						int offsetY = yCoord + y;
						int offsetZ = zCoord + z;
						
						TileEntity te = worldObj.getBlockTileEntity(offsetX, offsetY, offsetZ);
						if (te != null && te.getClass().getName() == "dan200.computer.shared.TileEntityCable") {
							if (peripheral == null) {
								worldObj.destroyBlock(offsetX, offsetY, offsetZ, true);
							}
						}
						
					}
				}	
			}
			
		}
	}

	@Override
	public String getType() {
		if (peripheral == null) {
			return null;
		}
		return peripheral.getType();
	}

	@Override
	public String[] getMethodNames() {
		if (peripheral == null) {
			return new String[0];
		}
		return peripheral.getMethodNames();
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception {
		if (peripheral == null) {
			return null;
		}
		return peripheral.callMethod(computer, method, arguments);
	}
	
	@Override
	public boolean canAttachToSide(int side) {
		return true;
	}

	@Override
	public void attach(IComputerAccess computer) {
		if (peripheral != null) {
			peripheral.attach(computer);
		}
	}

	@Override
	public void detach(IComputerAccess computer) {
		if (peripheral != null) {
			peripheral.detach(computer);
		}
	}

}

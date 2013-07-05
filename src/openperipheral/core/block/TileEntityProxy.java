package openperipheral.core.block;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import openperipheral.core.util.BlockUtils;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

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

		if (!worldObj.isRemote) {

			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {

						int offsetX = xCoord + x;
						int offsetY = yCoord + y;
						int offsetZ = zCoord + z;

						int tots = Math.abs(x) + Math.abs(y) + Math.abs(z);
						if (tots < -1 || tots > 1) {
							continue;
						}

						TileEntity te = worldObj.getBlockTileEntity(offsetX, offsetY, offsetZ);

						if (te != null && te.getClass().getName() == "dan200.computer.shared.TileEntityCable") {
							if (peripheral == null) {
								int blockId = worldObj.getBlockId(offsetX, offsetY, offsetZ);
								int meta = worldObj.getBlockMetadata(offsetX, offsetY, offsetZ);
								int subtype = getCableSubtypeFromMetadata(meta);
								if (subtype == 1 || subtype == 2) {
									BlockUtils.dropItemStackInWorld(worldObj, offsetX, offsetY, offsetZ, new ItemStack(blockId, 1, 1));

									if (subtype == 2) {
										BlockUtils.dropItemStackInWorld(worldObj, offsetX, offsetY, offsetZ, new ItemStack(blockId, 1, 0));
									}
									worldObj.setBlockToAir(offsetX, offsetY, offsetZ);
								}
							}
						}

					}
				}
			}

		}
	}

	public static int getCableSubtypeFromMetadata(int metadata) {
		if ((metadata >= 0) && (metadata < 6)) {
			return 1;
		}
		if ((metadata >= 6) && (metadata < 12)) {
			return 2;
		}

		return 0;
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

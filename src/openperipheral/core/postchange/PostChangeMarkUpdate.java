package openperipheral.core.postchange;

import net.minecraft.tileentity.TileEntity;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.interfaces.IPostChangeHandler;

public class PostChangeMarkUpdate implements IPostChangeHandler {

	@Override
	public void execute(Object target, IPeripheralMethodDefinition luaMethod, Object[] values) {

		if (target instanceof TileEntity && luaMethod.getCauseTileUpdate()) {
			TileEntity tile = (TileEntity) target;
			tile.worldObj.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
			tile.worldObj.notifyBlocksOfNeighborChange(tile.xCoord, tile.yCoord, tile.zCoord, tile.getBlockType().blockID);

		}
	}
}

package openperipheral.common.postchange;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IMethodDefinition;
import openperipheral.api.IPostChangeHandler;

public class PostChangeMarkUpdate implements IPostChangeHandler {

	@Override
	public void execute(TileEntity tile, IMethodDefinition luaMethod, Object[] values) {

		if (luaMethod.getCauseTileUpdate()) {
			tile.worldObj.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
			tile.worldObj.notifyBlocksOfNeighborChange(tile.xCoord, tile.yCoord, tile.zCoord, tile.getBlockType().blockID);

		}
	}
}

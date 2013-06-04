package openperipheral.postchange;

import net.minecraft.tileentity.TileEntity;
import openperipheral.IPostChangeHandler;
import openperipheral.definition.DefinitionMethod;

public class PostChangeMarkUpdate implements IPostChangeHandler {

	@Override
	public void execute(TileEntity tile, DefinitionMethod luaMethod,
			Object[] values) {

		if (luaMethod.getCauseTileUpdate()) {
			tile.worldObj.markBlockForUpdate(tile.xCoord, tile.yCoord,
					tile.zCoord);
		}
	}
}

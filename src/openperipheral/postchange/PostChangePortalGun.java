package openperipheral.postchange;

import net.minecraft.tileentity.TileEntity;
import openperipheral.IPostChangeHandler;
import openperipheral.definition.DefinitionMethod;
import openperipheral.definition.DefinitionMethod.CallType;

public class PostChangePortalGun implements IPostChangeHandler {

	@Override
	public void execute(TileEntity tile, DefinitionMethod luaMethod, Object[] values) {
		if (luaMethod.getCallType() == CallType.SET_PROPERTY) {
			String tileClassName = tile.getClass().getName();
			if (tileClassName == "portalgun.common.tileentity.TileEntityAFP") {
				tile.worldObj.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
				try {
					Object po = tile.getClass().getField("pair").get(tile);
					if (po != null) {
						TileEntity pair = (TileEntity) po;
						pair.getClass().getField(luaMethod.getPropertyName()).set(pair, values[0]);
						pair.worldObj.markBlockForUpdate(pair.xCoord, pair.yCoord, pair.zCoord);
					}
				} catch(Exception e) {
				}
			}
		}

	}
	
}

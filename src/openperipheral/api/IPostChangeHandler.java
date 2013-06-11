package openperipheral.api;

import net.minecraft.tileentity.TileEntity;
import openperipheral.common.definition.DefinitionMethod;

public interface IPostChangeHandler {
	public void execute(TileEntity tile, DefinitionMethod luaMethod, Object[] values);
}

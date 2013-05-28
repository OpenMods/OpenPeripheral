package openperipheral;

import openperipheral.definition.DefinitionMethod;
import net.minecraft.tileentity.TileEntity;

public interface IPostChangeHandler {
	public void execute(TileEntity tile, DefinitionMethod luaMethod, Object[] values);
}

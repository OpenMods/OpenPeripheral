package openperipheral;

import net.minecraft.tileentity.TileEntity;
import openperipheral.definition.DefinitionMethod;

public interface IPostChangeHandler {
	public void execute(TileEntity tile, DefinitionMethod luaMethod,
			Object[] values);
}

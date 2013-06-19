package openperipheral.api;

import net.minecraft.tileentity.TileEntity;

public interface IPostChangeHandler {
	public void execute(TileEntity tile, IMethodDefinition luaMethod, Object[] values);
}

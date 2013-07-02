package openperipheral.common.interfaces;

import net.minecraft.tileentity.TileEntity;

public interface IPostChangeHandler {
	public void execute(Object tile, IPeripheralMethodDefinition luaMethod, Object[] values);
}

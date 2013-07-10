package openperipheral.core.adapter;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityRecordPlayer;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import dan200.computer.api.IComputerAccess;

public class AdapterRecordPlayer implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return TileEntityRecordPlayer.class;
	}
	
	@LuaMethod
	public ItemStack getRecord(IComputerAccess computer, TileEntityRecordPlayer recordPlayer) {
		return recordPlayer.func_96097_a();
	}

}

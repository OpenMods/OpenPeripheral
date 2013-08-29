package openperipheral.core.adapter.vanilla;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityRecordPlayer;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

public class AdapterRecordPlayer implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return TileEntityRecordPlayer.class;
	}

	@LuaMethod(
			returnType = LuaType.TABLE,
			description = "Get the record currently being played")
	public ItemStack getRecord(IComputerAccess computer, TileEntityRecordPlayer recordPlayer) {
		return recordPlayer.func_96097_a();
	}

}

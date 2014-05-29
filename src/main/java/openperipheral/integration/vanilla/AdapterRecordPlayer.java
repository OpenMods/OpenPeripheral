package openperipheral.integration.vanilla;

import net.minecraft.item.ItemStack;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterRecordPlayer implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityRecordPlayer.class;
	}

	@LuaMethod(returnType = LuaType.TABLE, description = "Get the record currently being played")
	public ItemStack getRecord(TileEntityRecordPlayer recordPlayer) {
		return recordPlayer.func_96097_a();
	}

}

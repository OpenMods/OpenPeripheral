package openperipheral.core.adapter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import dan200.computer.api.IComputerAccess;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.core.block.TileEntityPlayerInventory;

public class AdapterPlayerInventory implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityPlayerInventory.class;
	}
	
	@LuaMethod(returnType = LuaType.NUMBER, description = "Gets the index of the currently held item (1-9)")
	public Integer getSelectedSlot(IComputerAccess computer, TileEntityPlayerInventory pim) {
		if (pim == null) { return null; }
		EntityPlayer player = pim.getPlayer();
		if (player == null) { return null; }
		int slot = player.inventory.currentItem;
		if (slot >= 0 && slot < InventoryPlayer.getHotbarSize()) { return slot + 1; }
		return null;
	}
}

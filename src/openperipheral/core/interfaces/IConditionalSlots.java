package openperipheral.core.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IConditionalSlots {
	public boolean isStackValidForSlot(int slot, ItemStack stack);
	public boolean canTakeStack(int slotNumber, EntityPlayer player);
}

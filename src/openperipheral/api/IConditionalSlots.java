package openperipheral.api;

import net.minecraft.item.ItemStack;

public interface IConditionalSlots {
	public boolean isStackValidForSlot(int slot, ItemStack stack);
}

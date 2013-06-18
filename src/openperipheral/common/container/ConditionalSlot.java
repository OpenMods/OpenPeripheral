package openperipheral.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import openperipheral.api.IConditionalSlots;

public class ConditionalSlot extends Slot {

	public ConditionalSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack itemStack) {
		if (this.inventory instanceof IConditionalSlots) {
			return ((IConditionalSlots) inventory).isStackValidForSlot(slotNumber, itemStack);
		}
		return true;
	}

}

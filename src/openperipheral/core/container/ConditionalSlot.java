package openperipheral.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import openperipheral.core.interfaces.IConditionalSlots;

public class ConditionalSlot extends Slot {

	public ConditionalSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack itemStack) {
		if (this.inventory instanceof IConditionalSlots) { return ((IConditionalSlots)inventory).isValidForSlot(slotNumber, itemStack); }
		return true;
	}

	public boolean canTakeStack(EntityPlayer player) {
		if (this.inventory instanceof IConditionalSlots) { return ((IConditionalSlots)inventory).canTakeStack(slotNumber, player); }
		return true;
	}

}

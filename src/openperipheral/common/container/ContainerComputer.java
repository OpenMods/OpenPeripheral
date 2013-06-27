package openperipheral.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;

public class ContainerComputer extends Container {

	public ContainerComputer() {
	    addSlotToContainer(new Slot(new InventoryBasic("fakeInventory", false, 1), 0, -999, -999));
	}
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}

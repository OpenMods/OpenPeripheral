package openperipheral.robots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import openperipheral.api.IRobot;
import openperipheral.core.container.ContainerGeneric;

public class ContainerRobot extends ContainerGeneric {

	public ContainerRobot(IInventory playerInventory, IRobot robot) {
		super(playerInventory, robot.getInventory(), new int[0]);
	}

	@Override
	protected void addInventorySlots(int[] slots) {
		for (int l = 0; l < 3; l++) {
			for (int k1 = 0; k1 < 9; k1++) {
				addSlotToContainer(new Slot(inventory, k1 + l * 9, 8 + k1 * 18, 16 + l * 18));
			}
		}
	}
}

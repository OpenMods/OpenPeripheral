package openperipheral.common.robotupgrades.lazers;

import java.util.ArrayList;

import net.minecraft.entity.EntityCreature;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openperipheral.OpenPeripheral;
import openperipheral.api.IRestriction;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.common.entity.EntityLazer;
import openperipheral.common.item.meta.MetaEnergyCell;

public class MethodLazerFire implements IRobotMethod {

	@Override
	public boolean needsSanitize() {
		return true;
	}

	@Override
	public ArrayList<IRestriction> getRestrictions(int index) {
		return null;
	}

	@Override
	public String getLuaName() {
		return "fire";
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public Class[] getRequiredParameters() {
		return null;
	}

	@Override
	public Object execute(IRobotUpgradeInstance instance, Object[] args) throws Exception {
		InstanceLazersUpgrade upgrade = (InstanceLazersUpgrade) instance;
		IRobot robot = upgrade.getRobot();
		IInventory inventory = robot.getInventory();
		ItemStack cellStack = null;
		if (upgrade.isOverheated()) {
			return false;
		}
		if (!upgrade.isCoolEnough()) {
			upgrade.setOverheated(true);
			return false;
		}
		int cellIndex = 0;
		for (int i=0; i < inventory.getSizeInventory() - 1; i++) {
			ItemStack slot = inventory.getStackInSlot(i);
			if (slot != null && slot.stackSize > 0 && OpenPeripheral.Items.generic.isA(slot, MetaEnergyCell.class)) {
				cellStack = slot;
				cellIndex = i;
				break;
			}
		}
		if (cellStack != null) {
			EntityCreature entity = ((InstanceLazersUpgrade)instance).getEntity();
			entity.playSound("openperipheral.lazer", 1F, entity.worldObj.rand.nextFloat() + 0.4f);
			EntityLazer lazer = new EntityLazer(entity.worldObj, entity);
			entity.worldObj.spawnEntityInWorld(lazer);
			cellStack.stackSize--;
			if (cellStack.stackSize == 0) {
				inventory.setInventorySlotContents(cellIndex, null);
			}
			robot.setWeaponSpinSpeed(1.0f);
			upgrade.modifyHeat(3);
			return true;
		}
		return false;
	}

}

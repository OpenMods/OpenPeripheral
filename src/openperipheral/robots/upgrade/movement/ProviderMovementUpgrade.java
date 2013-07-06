package openperipheral.robots.upgrade.movement;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderMovementUpgrade implements IRobotUpgradeProvider {
	
	private HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
	
	public ProviderMovementUpgrade() {
		items.put(1, new ItemStack(Item.bootsIron));
		items.put(2, new ItemStack(Item.bootsDiamond));
	}
	
	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot, int tier) {
		return new InstanceMovementUpgrade(robot);
	}

	@Override
	public String getUpgradeId() {
		return "movement";
	}

	@Override
	public Map<Integer, ItemStack> getUpgradeItems() {
		return items;
	}

	@Override
	public boolean isForced() {
		return false;
	}

	@Override
	public boolean isApplicableForRobot(IRobot robot) {
		return true;
	}

	@Override
	public Class getUpgradeClass() {
		return InstanceMovementUpgrade.class;
	}

}

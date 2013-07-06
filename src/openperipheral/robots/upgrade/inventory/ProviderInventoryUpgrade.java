package openperipheral.robots.upgrade.inventory;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderInventoryUpgrade implements IRobotUpgradeProvider {
	
	private HashMap<Integer, ItemStack> upgradeItems;
	
	public ProviderInventoryUpgrade() {
		
		upgradeItems = new HashMap<Integer, ItemStack>();
		upgradeItems.put(1, new ItemStack(Block.hopperBlock));
		upgradeItems.put(2, new ItemStack(Block.chest));
	}

	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot, int tier) {
		return new InstanceInventoryUpgrade(robot);
	}

	@Override
	public String getUpgradeId() {
		return "inventory";
	}

	@Override
	public boolean isForced() {
		return false;
	}

	@Override
	public Map<Integer, ItemStack> getUpgradeItems() {
		return upgradeItems;
	}

	@Override
	public boolean isApplicableForRobot(IRobot robot) {
		return true;
	}

	@Override
	public Class getUpgradeClass() {
		return InstanceInventoryUpgrade.class;
	}

}

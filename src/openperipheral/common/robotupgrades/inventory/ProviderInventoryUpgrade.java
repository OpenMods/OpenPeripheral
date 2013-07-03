package openperipheral.common.robotupgrades.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderInventoryUpgrade implements IRobotUpgradeProvider {

	private ArrayList<IRobotMethod> methods;
	
	private HashMap<Integer, ItemStack> upgradeItems;
	
	public ProviderInventoryUpgrade() {
		methods = new ArrayList<IRobotMethod>();
		methods.add(new MethodDrop());
		methods.add(new MethodMoveItem("pushItem", true));
		methods.add(new MethodMoveItem("pullItem", false));
		
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
	public List<IRobotMethod> getMethods() {
		return methods;
	}

	@Override
	public Map<Integer, ItemStack> getUpgradeItems() {
		return upgradeItems;
	}

}

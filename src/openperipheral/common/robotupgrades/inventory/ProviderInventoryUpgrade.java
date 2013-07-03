package openperipheral.common.robotupgrades.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderInventoryUpgrade implements IRobotUpgradeProvider {

	private ArrayList<IRobotMethod> methods;
	
	public ProviderInventoryUpgrade() {
		methods = new ArrayList<IRobotMethod>();
		methods.add(new MethodDrop());
		methods.add(new MethodMoveItem("pushItem", true));
		methods.add(new MethodMoveItem("pullItem", false));
	}

	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot) {
		return new InstanceInventoryUpgrade(robot);
	}

	@Override
	public String getUpgradeId() {
		return "inventory";
	}

	@Override
	public ItemStack getUpgradeItem() {
		return new ItemStack(Block.chest);
	}

	@Override
	public boolean isForced() {
		return false;
	}

	@Override
	public List<IRobotMethod> getMethods() {
		return methods;
	}

}

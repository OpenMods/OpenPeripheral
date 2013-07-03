package openperipheral.common.robotupgrades.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderMovementUpgrade implements IRobotUpgradeProvider {

	ArrayList<IRobotMethod> methods;
	
	private HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
	
	public ProviderMovementUpgrade() {
		methods = new ArrayList<IRobotMethod>();
		methods.add(new MethodGoto());
		methods.add(new MethodJump());
		methods.add(new MethodSetPitch());
		methods.add(new MethodSetYaw());
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
	public List<IRobotMethod> getMethods() {
		return methods;
	}

	@Override
	public boolean isForced() {
		return false;
	}

}

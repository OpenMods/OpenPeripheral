package openperipheral.common.robotupgrades.lazers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openperipheral.api.EnumRobotType;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderLazersUpgrade implements IRobotUpgradeProvider {

	ArrayList<IRobotMethod> methods;
	HashMap<Integer, ItemStack> upgradeItems;
	
	public ProviderLazersUpgrade() {
		
		methods = new ArrayList<IRobotMethod>();
		methods.add(new MethodFireLazer());
		methods.add(new MethodFireExplosive());
		methods.add(new MethodGetHeat());
		methods.add(new MethodGetLazerCoolingPerTick());
		methods.add(new MethodMaxLazerHeat());
		
		//TODO: proper items
		upgradeItems = new HashMap<Integer, ItemStack>();
		upgradeItems.put(1, new ItemStack(Item.axeGold));
		upgradeItems.put(2, new ItemStack(Item.axeDiamond));
	}
	
	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot, int tier) {
		return new InstanceLazersUpgrade(robot, tier);
	}

	@Override
	public String getUpgradeId() {
		return "lazers";
	}

	@Override
	public List<IRobotMethod> getMethods() {
		return methods;
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

}

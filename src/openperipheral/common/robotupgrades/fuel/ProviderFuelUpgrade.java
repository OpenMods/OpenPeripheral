package openperipheral.common.robotupgrades.fuel;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderFuelUpgrade implements IRobotUpgradeProvider {

	private ArrayList<IRobotMethod> methods;
	public ProviderFuelUpgrade() {
		methods = new ArrayList<IRobotMethod>();
		methods.add(new MethodGetFuelLevel());
		methods.add(new MethodRefuel());
	}
	
	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot) {
		return new InstanceFuelUpgrade(robot);
	}

	@Override
	public String getUpgradeId() {
		return "fuel";
	}

	@Override
	public ItemStack getUpgradeItem() {
		return null;
	}

	@Override
	public List<IRobotMethod> getMethods() {
		return methods;
	}

	@Override
	public boolean isForced() {
		return true;
	}

}

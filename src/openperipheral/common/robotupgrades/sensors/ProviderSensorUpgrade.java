package openperipheral.common.robotupgrades.sensors;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.common.peripheral.SensorPeripheral;

public class ProviderSensorUpgrade implements IRobotUpgradeProvider {

	private ArrayList<IRobotMethod> methods;
	
	public ProviderSensorUpgrade() {
		methods = new ArrayList<IRobotMethod>();
		methods.add(new MethodSensorProxy("getPlayerNames") {
			@Override
			public Object executeOnSensor(SensorPeripheral sensor, Object[] args) throws Exception {
				return sensor.getPlayerNames();
			}
		});
		methods.add(new MethodSensorProxy("getPlayerData") {
			@Override
			public Object executeOnSensor(SensorPeripheral sensor, Object[] args) throws Exception {
				return sensor.getPlayerData((String) args[0]);
			}
		});
	}
	
	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot) {
		return new InstanceSensorUpgrade(robot);
	}

	@Override
	public String getUpgradeId() {
		return "sensors";
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
		return false;
	}

}

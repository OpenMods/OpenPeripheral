package openperipheral.sensor;

import net.minecraft.world.World;
import openperipheral.core.peripheral.HostedPeripheral;
import openperipheral.turtle.TurtleSensorEnvironment;

public class SensorPeripheral extends HostedPeripheral {

	public SensorPeripheral(Object targetObject) {
		super(targetObject);
	}

	@Override
	public World getWorldObject() {
		return ((TurtleSensorEnvironment)targetObject).getWorld();
	}
	
}

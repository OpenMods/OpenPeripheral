package openperipheral.common.robotupgrades.sensors;

import java.util.HashMap;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.common.interfaces.ISensorEnvironment;
import openperipheral.common.peripheral.SensorPeripheral;

public class InstanceSensorUpgrade implements IRobotUpgradeInstance, ISensorEnvironment {

	private SensorPeripheral sensorPeripheral;
	private IRobot robot;
	
	public InstanceSensorUpgrade(IRobot robot) {
		sensorPeripheral = new SensorPeripheral(this);
		this.robot = robot;
	}
	
	public SensorPeripheral getSensor() {
		return sensorPeripheral;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub

	}

	@Override
	public HashMap<Integer, EntityAIBase> getAITasks() {
		return null;
	}

	@Override
	public void update() {
		sensorPeripheral.update();
	}

	@Override
	public boolean isTurtle() {
		return false;
	}

	@Override
	public Vec3 getLocation() {
		return robot.getLocation();
	}

	@Override
	public World getWorld() {
		return robot.getWorld();
	}

	@Override
	public int getSensorRange() {
		return 5;
	}

	@Override
	public void onTierChanged(int tier) {
		// TODO Auto-generated method stub
		
	}

}

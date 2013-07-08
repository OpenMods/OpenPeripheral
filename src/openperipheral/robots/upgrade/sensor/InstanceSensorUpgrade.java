package openperipheral.robots.upgrade.sensor;

import java.util.HashMap;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.LuaMethod;
import openperipheral.core.interfaces.ISensorEnvironment;
import openperipheral.core.peripheral.SensorPeripheral;

public class InstanceSensorUpgrade implements IRobotUpgradeInstance, ISensorEnvironment {

	private SensorPeripheral sensorPeripheral;
	private IRobot robot;
	private int tier;
	
	public InstanceSensorUpgrade(IRobot robot, int tier) {
		sensorPeripheral = new SensorPeripheral(this, robot.getEntity());
		this.robot = robot;
		this.tier = tier;
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
	@LuaMethod
	public int getSensorRange() {
		return 10 * this.tier;
	}

	@Override
	public void onTierChanged(int tier) {
		this.tier = tier;
	}
	
	@LuaMethod(onTick=false)
	public String[] getPlayerNames() throws Exception {
		if (tier < 2) {
			throw new Exception("At least a tier 2 sensor upgrade required");
		}
		return sensorPeripheral.getPlayerNames();
	}
	
	@LuaMethod(onTick=false)
	public HashMap getPlayerData(String playerName) throws Exception {
		if (tier < 2) {
			throw new Exception("At least a tier 2 sensor upgrade required");
		}
		return sensorPeripheral.getPlayerData(playerName);
	}
	
	@LuaMethod(onTick=false)
	public Integer[] getMobIds() {
		return sensorPeripheral.getMobIds();
	}
	
	@LuaMethod(onTick=false)
	public HashMap getMobData(int mobId) {
		return sensorPeripheral.getMobData(mobId);
	}
	
	@LuaMethod(onTick=false)
	public Integer[] getMinecartIds() {
		return sensorPeripheral.getMinecartIds();
	}
	
	@LuaMethod(onTick=false)
	public HashMap getMinecartData(int minecartId) {
		return sensorPeripheral.getMinecartData(minecartId);
	}

	@LuaMethod
	public HashMap sonicScan() {
		return sensorPeripheral.sonicScan();
	}
}

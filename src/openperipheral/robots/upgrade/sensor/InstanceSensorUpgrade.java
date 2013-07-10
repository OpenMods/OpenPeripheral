package openperipheral.robots.upgrade.sensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.LuaMethod;
import openperipheral.core.adapter.AdapterSensor;
import openperipheral.core.interfaces.ISensorEnvironment;
import openperipheral.sensor.SensorPeripheral;

public class InstanceSensorUpgrade implements IRobotUpgradeInstance, ISensorEnvironment {

	private AdapterSensor adapter;
	private IRobot robot;
	private int tier;
	
	public InstanceSensorUpgrade(IRobot robot, int tier) {
		adapter = new AdapterSensor();
		this.robot = robot;
		this.tier = tier;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
	}

	@Override
	public HashMap<Integer, EntityAIBase> getAITasks() {
		return null;
	}

	@Override
	public void update() {
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
	public ArrayList<String> getPlayerNames() throws Exception {
		if (tier < 2) {
			throw new Exception("At least a tier 2 sensor upgrade required");
		}
		return adapter.getPlayerNames(null, this);
	}
	
	@LuaMethod(onTick=false)
	public Map getPlayerData(String playerName) throws Exception {
		if (tier < 2) {
			throw new Exception("At least a tier 2 sensor upgrade required");
		}
		return adapter.getPlayerData(null, this, playerName);
	}
	
	@LuaMethod(onTick=false)
	public ArrayList<Integer> getMobIds() {
		return adapter.getMobIds(null, this);
	}
	
	@LuaMethod(onTick=false)
	public Map getMobData(int mobId) {
		return adapter.getMobData(null, this, mobId);
	}
	
	@LuaMethod(onTick=false)
	public ArrayList<Integer> getMinecartIds() {
		return adapter.getMinecartIds(null, this);
	}
	
	@LuaMethod(onTick=false)
	public Map getMinecartData(int minecartId) {
		return adapter.getMinecartData(null, this, minecartId);
	}

	@LuaMethod
	public Map sonicScan() {
		return adapter.sonicScan(null, this);
	}
}

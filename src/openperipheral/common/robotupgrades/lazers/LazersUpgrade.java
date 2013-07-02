package openperipheral.common.robotupgrades.lazers;

import java.util.HashMap;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobot;

public class LazersUpgrade implements IRobotUpgradeInstance {

	private IRobot robot;
	
	public LazersUpgrade(IRobot robot) {
		this.robot = robot;
		System.out.println("Created lazer upgrade");
	}
	
	public IRobot getRobot() {
		return robot;
	}
	
	public EntityCreature getEntity() {
		return getRobot().getEntity();
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
		// TODO Auto-generated method stub

	}

}

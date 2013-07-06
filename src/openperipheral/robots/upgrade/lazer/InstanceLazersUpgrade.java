package openperipheral.robots.upgrade.lazer;

import java.util.HashMap;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openperipheral.OpenPeripheral;
import openperipheral.api.ILazerRobot;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.LuaMethod;
import openperipheral.core.item.ItemGeneric;
import openperipheral.core.item.ItemGeneric.Metas;
import openperipheral.robots.entity.EntityLazer;

public class InstanceLazersUpgrade implements IRobotUpgradeInstance {

	private static final String TAG_OVERHEATED = "o";
	private static final String TAG_HEAT = "h";
	
	private ILazerRobot robot;
	
	/**
	 * The current heat of the lazer
	 */
	private double heat = 0;
	
	/**
	 * The highest upgrade tier in the inventory
	 */
	private int tier = 0;
	
	/**
	 * Is the gun currently overheated
	 */
	private boolean isOverheated = false;
	
	
	public InstanceLazersUpgrade(IRobot robot, int tier) {
		this.robot = (ILazerRobot)robot;
		this.tier = tier;
	}
	
	public ILazerRobot getRobot() {
		return robot;
	}
	
	public int getTier() {
		return tier;
	}
	
	public EntityCreature getEntity() {
		return getRobot().getEntity();
	}
	
	@LuaMethod
	public double getHeat() {
		return heat;
	}
	
	public void modifyHeat(double mod) {
		heat += mod;
		heat = Math.max(0, heat);
	}
	
	@LuaMethod
	public boolean isCoolEnough() {
		return heat <= getMaxHeat();
	}
	
	@LuaMethod
	public double getMaxHeat() {
		return tier * 15;
	}
	
	@LuaMethod
	public double getCoolingPerTick() {
		return .1 + (tier * 0.02);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setDouble(TAG_HEAT, heat);
		nbt.setBoolean(TAG_OVERHEATED, isOverheated);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey(TAG_HEAT)) {
			heat = nbt.getDouble(TAG_HEAT);
		}
		if (nbt.hasKey(TAG_OVERHEATED)) {
			isOverheated = nbt.getBoolean(TAG_OVERHEATED);
		}
	}

	@Override
	public HashMap<Integer, EntityAIBase> getAITasks() {
		return null;
	}

	@Override
	public void update() {
		getRobot().modifyWeaponSpinSpeed(-0.01f);
		modifyHeat(-getCoolingPerTick());
		if (heat == 0 && isOverheated) {
			isOverheated = false;
		}
	}

	@Override
	public void onTierChanged(int tier) {
		this.tier = tier;
	}

	@LuaMethod
	public boolean isOverheated() {
		return isOverheated;
	}

	public void setOverheated(boolean overheated) {
		isOverheated = overheated;
	}
	
	@LuaMethod
	public boolean fire() {
		return fireLazer(ItemGeneric.Metas.energyCell, false, 3);
	}

	@LuaMethod
	public boolean fireExplosive() {
		return fireLazer(ItemGeneric.Metas.energyCell, true, 7);
	}
	
	public boolean fireLazer(Metas ammoItem, boolean isExplosive, double heatModifier) {
		IInventory inventory = robot.getInventory();
		ItemStack cellStack = null;
		if (isOverheated()) {
			return false;
		}
		if (!isCoolEnough()) {
			setOverheated(true);
			return false;
		}
		int cellIndex = 0;
		for (int i=0; i < inventory.getSizeInventory() - 1; i++) {
			ItemStack slot = inventory.getStackInSlot(i);
			if (slot != null && slot.stackSize > 0 && OpenPeripheral.Items.generic.isA(slot, ammoItem)) {
				cellStack = slot;
				cellIndex = i;
				break;
			}
		}
		if (cellStack != null) {
			EntityCreature entity = robot.getEntity();
			entity.playSound("openperipheral.lazer", 1F, entity.worldObj.rand.nextFloat() + 0.4f);
			EntityLazer lazer = new EntityLazer(entity.worldObj, entity);
			lazer.setExplosive(isExplosive);
			entity.worldObj.spawnEntityInWorld(lazer);
			cellStack.stackSize--;
			if (cellStack.stackSize == 0) {
				inventory.setInventorySlotContents(cellIndex, null);
			}
			robot.setWeaponSpinSpeed(1.0f);
			modifyHeat(heatModifier);
			return true;
		}
		return false;
	}

}

package openperipheral.robots.upgrade.lazer;

import java.util.HashMap;

import dan200.computer.api.IComputerAccess;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openperipheral.OpenPeripheral;
import openperipheral.api.ILazerRobot;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.item.ItemGeneric;
import openperipheral.core.item.ItemGeneric.Metas;
import openperipheral.robots.entity.EntityLazer;

public class AdapterLazersUpgrade implements IRobotUpgradeAdapter {

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
	
	
	public AdapterLazersUpgrade(IRobot robot, int tier) {
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
	public double getHeat(IComputerAccess computer, IRobot robot) {
		return heat;
	}
	
	public void modifyHeat(double mod) {
		heat += mod;
		heat = Math.max(0, heat);
	}
	
	@LuaMethod
	public boolean isCoolEnough(IComputerAccess computer, IRobot robot) {
		return heat <= getMaxHeat(computer, robot);
	}
	
	@LuaMethod
	public double getMaxHeat(IComputerAccess computer, IRobot robot) {
		return tier * 15;
	}
	
	@LuaMethod
	public double getCoolingPerTick(IComputerAccess computer, IRobot robot) {
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
		modifyHeat(-getCoolingPerTick(null, null));
		if (heat == 0 && isOverheated) {
			isOverheated = false;
		}
	}

	@Override
	public void onTierChanged(int tier) {
		this.tier = tier;
	}

	@LuaMethod
	public boolean isOverheated(IComputerAccess computer, IRobot robot) {
		return isOverheated;
	}

	public void setOverheated(boolean overheated) {
		isOverheated = overheated;
	}
	
	@LuaMethod
	public boolean fireLight(IComputerAccess computer, IRobot robot) {
		return fireLazer(ItemGeneric.Metas.lightEnergyCell, false, false, 2);
	}
	
	@LuaMethod
	public boolean fireMedium(IComputerAccess computer, IRobot robot) throws Exception {
		if (tier < 2) {
			throw new Exception("At least a tier 2 lazer upgrade required");
		}
		return fireLazer(ItemGeneric.Metas.mediumEnergyCell, true, false, 3);
	}

	@LuaMethod
	public boolean fireHeavy(IComputerAccess computer, IRobot robot) throws Exception {
		if (tier < 3) {
			throw new Exception("At least a tier 3 lazer upgrade required");
		}
		return fireLazer(ItemGeneric.Metas.heavyEnergyCell, true, true, 4);
	}
	
	public boolean fireLazer(Metas ammoItem, boolean canDamageBlocks, boolean isExplosive, double heatModifier) {
		IInventory inventory = robot.getInventory();
		ItemStack cellStack = null;
		if (isOverheated(null, null)) {
			return false;
		}
		if (!isCoolEnough(null, null)) {
			setOverheated(true);
			return false;
		}
		int cellIndex = 0;
		for (int i=0; i < inventory.getSizeInventory(); i++) {
			ItemStack slot = inventory.getStackInSlot(i);
			if (slot != null && slot.stackSize > 0 && OpenPeripheral.Items.generic.isA(slot, ammoItem)) {
				cellStack = slot;
				cellIndex = i;
				break;
			}
		}
		if (cellStack != null) {
			EntityCreature entity = robot.getEntity();
			entity.playSound("openperipheral:lazer", 1F, entity.worldObj.rand.nextFloat() + 0.4f);
			EntityLazer lazer = new EntityLazer(entity.worldObj, entity);
			lazer.setExplosive(isExplosive);
			lazer.setDamageBlocks(canDamageBlocks);
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

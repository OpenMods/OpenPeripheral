package openperipheral.robots.common.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openperipheral.OpenPeripheral;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.api.RobotUpgradeManager;
import openperipheral.core.ConfigSettings;
import openperipheral.core.OPInventory;
import openperipheral.core.interfaces.IInventoryCallback;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.util.BlockUtils;
import openperipheral.robots.common.RobotPeripheralMethod;
import openperipheral.robots.common.block.TileEntityRobot;

public abstract class EntityRobot extends EntityCreature implements IRobot, IInventoryCallback {

	/**
	 * the location and id of the controller
	 */
	private int controllerX = 0;
	private int controllerY = 0;
	private int controllerZ = 0;
	private String controllerUuid = "[none]";
	private boolean linkedToController = false;
	private int robotId = 0;

	private float fuelLevel = 0;

	private int maxHealth = 50;

	/**
	 * The main inventory
	 */
	protected OPInventory inventory = new OPInventory("robot", false, 27);

	/**
	 * A map of currently installed instanceIds to module instance objects
	 */
	private HashMap<String, IRobotUpgradeInstance> upgradeInstances;

	/**
	 * A map of currently installed instanceIds to their tiers
	 */
	private HashMap<String, Integer> upgradeTiers;

	/**
	 * map of method name to upgrade instance, so we know what object handles
	 * the called method
	 */
	private HashMap<String, IRobotUpgradeInstance> upgradeMethodMap;

	/**
	 * a map of upgrade instance id to upgrade AI objects. We need this to make
	 * sure we can remove any when the upgrade is disabled
	 */
	private HashMap<EntityAIBase, IRobotUpgradeInstance> upgradeAIMap;

	private NBTTagCompound upgradesNBT = new NBTTagCompound();

	public EntityRobot(World par1World) {
		super(par1World);
		upgradeMethodMap = new HashMap<String, IRobotUpgradeInstance>();
		upgradeInstances = new HashMap<String, IRobotUpgradeInstance>();
		upgradeAIMap = new HashMap<EntityAIBase, IRobotUpgradeInstance>();
		upgradeTiers = new HashMap<String, Integer>();
		this.health = this.getMaxHealth();
		this.setSize(1F, 3F);
		this.moveSpeed = 0.22F;
		this.getNavigator().setAvoidsWater(true);
		inventory.addCallback(this);
		this.texture = String.format("%s/models/robot.png", ConfigSettings.TEXTURES_PATH);
	}

	/**
	 * Get the entity move speed
	 */
	@Override
	public float getMoveSpeed() {
		return moveSpeed;
	}

	/**
	 * Set the entity move speed
	 */
	@Override
	public void setMoveSpeed(float speed) {
		moveSpeed = speed;
	}

	/**
	 * Get the current fuel level
	 * 
	 * @return the fuel level
	 */
	@Override
	public float getFuelLevel() {
		return fuelLevel;
	}

	/**
	 * set the fuel level
	 * 
	 * @param fuel
	 */
	@Override
	public void setFuelLevel(float fuel) {
		fuelLevel = fuel;
	}

	/**
	 * add or remove fuel
	 * 
	 * @param fuel
	 */
	@Override
	public void modifyFuelLevel(float fuel) {
		fuelLevel += fuel;
	}

	/**
	 * Set the robots pitch
	 */
	@Override
	public void setPitch(float pitch) {
		rotationPitch = pitch;
		rotationPitch = Math.max(rotationPitch, -45);
		rotationPitch = Math.min(rotationPitch, 45);
	}

	/**
	 * Get the current entity pitch
	 */
	@Override
	public float getPitch() {
		return rotationPitch;
	}

	/**
	 * Set the robots yaw
	 */
	@Override
	public void setYaw(float yaw) {
		renderYawOffset = prevRotationYawHead = rotationYawHead = prevRotationYaw = rotationYaw = yaw;
	}

	@Override
	public float getYaw() {
		return rotationYaw;
	}

	/**
	 * Get the inventory
	 */
	@Override
	public IInventory getInventory() {
		return inventory;
	}

	/**
	 * This is called whenever the inventory is changed
	 */
	@Override
	public void onInventoryChanged(IInventory inventory, int slotNumber) {

		if (worldObj.isRemote) {
			return;
		}

		HashMap<String, Integer> tiers = new HashMap<String, Integer>();

		// check for any forced upgrade and just apply them automatically at
		// tier 1
		for (IRobotUpgradeProvider provider : RobotUpgradeManager.getProviders()) {
			if (provider.isForced()) {
				tiers.put(provider.getUpgradeId(), 1);
			}
		}

		// loop through the items in the inventory and find all of the
		// providers that match a given itemstack. If multiple items from
		// a single provider is available, we find the one with the highest
		// tier and use that
		for (int i = 0; i < inventory.getSizeInventory() - 1; i++) {

			ItemStack stack = inventory.getStackInSlot(i);
			IRobotUpgradeProvider provider = RobotUpgradeManager.getProviderForStack(stack);

			if (provider == null || !provider.isApplicableForRobot(this)) {
				continue;
			}

			String providerId = provider.getUpgradeId();
			int tier = RobotUpgradeManager.getTierForUpgradeItem(provider, stack);
			if (tier != -1) {
				if (tiers.containsKey(providerId)) {
					int cTier = tiers.get(providerId);
					tier = Math.max(tier, cTier);
				}
				tiers.put(providerId, tier);
			}
		}

		// for each of the upgrade providers, if we havent already for that
		// upgrade installed, we create a new instance of it and let it read it's
		// data from the nbt
		for (Entry<String, Integer> tierEntry : tiers.entrySet()) {

			String providerId = tierEntry.getKey();
			int tier = tierEntry.getValue();

			IRobotUpgradeProvider provider = RobotUpgradeManager.getProviderById(providerId);

			if (!upgradeInstances.containsKey(providerId)) {

				// create a new instance
				IRobotUpgradeInstance instance = provider.provideUpgradeInstance(this, tier);

				// check if there's any information stored in our upgrades nbt
				if (upgradesNBT.hasKey(providerId)) {

					// let it read the nbt tag
					instance.readFromNBT(upgradesNBT.getCompoundTag(providerId));
				}

				// add all the methods to our method map
				for (IPeripheralMethodDefinition method : RobotPeripheralMethod.getMethodsForProvider(provider)) {
					upgradeMethodMap.put(method.getLuaName(), instance);
				}

				HashMap<Integer, EntityAIBase> aiTasks = instance.getAITasks();
				if (aiTasks != null) {
					for (Entry<Integer, EntityAIBase> entry : aiTasks.entrySet()) {
						tasks.addTask(entry.getKey(), entry.getValue());
						upgradeAIMap.put(entry.getValue(), instance);
					}
				}

				// add the instance to our instances list
				upgradeInstances.put(provider.getUpgradeId(), instance);

			} else {

				// If the upgrade is already enabled, but we insert a different
				// tier we notify the upgrade instance
				if (upgradeTiers.containsKey(providerId)) {
					int currentTier = upgradeTiers.get(providerId);
					if (currentTier != tier) {
						IRobotUpgradeInstance instance = upgradeInstances.get(providerId);
						if (instance != null) {
							instance.onTierChanged(tier);
						}
					}
				}
			}
		}

		upgradeTiers.clear();
		upgradeTiers.putAll(tiers);

		Iterator<Entry<String, IRobotUpgradeInstance>> upgradeIterator = upgradeInstances.entrySet().iterator();
		while (upgradeIterator.hasNext()) {
			Entry<String, IRobotUpgradeInstance> upgradeEntry = upgradeIterator.next();
			Set<String> validInstances = tiers.keySet();
			String instanceKey = upgradeEntry.getKey();
			if (!validInstances.contains(instanceKey)) {

				IRobotUpgradeInstance instanceV = upgradeInstances.get(instanceKey);

				// remove any lingering methods
				Iterator<Entry<String, IRobotUpgradeInstance>> iterator = upgradeMethodMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, IRobotUpgradeInstance> entry = iterator.next();
					if (entry.getValue() == instanceV) {
						iterator.remove();
					}
				}

				// remove any lingering AI
				Iterator<Entry<EntityAIBase, IRobotUpgradeInstance>> aiIterator = upgradeAIMap.entrySet().iterator();
				while (aiIterator.hasNext()) {
					Entry<EntityAIBase, IRobotUpgradeInstance> entry = aiIterator.next();
					if (entry.getValue() == instanceV) {
						this.tasks.removeTask(entry.getKey());
						aiIterator.remove();
					}
				}
				upgradeIterator.remove();
			}
		}

	}

	/**
	 * Get what the entity is looking at. Currently works for blocks, but we need
	 * to add logic for checking for entities too
	 */
	@Override
	public MovingObjectPosition getLookingAt() {
		Vec3 posVec = getLocation();
		posVec.yCoord += getEyeHeight();
		Vec3 lookVec = getLook(1.0f);
		// TODO: robots should be able to drop into other robots, so we need to
		// add logic for checking entities too
		Vec3 targetVec = posVec.addVector(lookVec.xCoord * 10f, lookVec.yCoord * 10f, lookVec.zCoord * 10f);
		return worldObj.rayTraceBlocks(posVec, targetVec);
	}

	/**
	 * Path finding range
	 */
	@Override
	protected int func_96121_ay() {
		return 32;
	}

	/**
	 * doesn't appear to work
	 */
	@Override
	public boolean canBePushed() {
		return false;
	}

	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (!worldObj.isRemote) {

			// check we can still find the controller
			TileEntityRobot controller = (TileEntityRobot) getController();
			if (controller != null) {
				controller.registerRobot(robotId, this);
			}
			linkedToController = controller != null;

			// update all the modules
			for (IRobotUpgradeInstance upgradeInstance : upgradeInstances.values()) {
				upgradeInstance.update();
			}

		}
	}

	// TODO: add this as a packed boolean in the dataWatcher.
	// lets change the model if he's not linked up!
	@Override
	public boolean isLinkedToController() {
		return linkedToController;
	}

	protected void updateAITasks() {
		// temporarily store the pitch, because the super method sets it to 0
		float oldPitch = rotationPitch;
		super.updateAITasks();
		this.rotationPitch = oldPitch;
	}

	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	protected void playStepSound(int par1, int par2, int par3, int par4) {
		this.playSound("openperipheral.robotstepping", 1F, 1F);
	}

	@Override
	protected String getDeathSound() {
		return "openperipheral.robotdead";
	}

	@Override
	protected String getHurtSound() {
		return "openperipheral.robothurt";
	}

	/**
	 * Get the maximum health of the entity
	 */
	@Override
	public int getMaxHealth() {
		return maxHealth;
	}

	/**
	 * Set the maximum health of the robot
	 */
	@Override
	public void setMaxHealth(int health) {
		maxHealth = health;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public TileEntity getController() {
		if (worldObj.blockExists(controllerX, controllerY, controllerZ)) {
			TileEntity tile = worldObj.getBlockTileEntity(controllerX, controllerY, controllerZ);
			if (tile != null && tile instanceof TileEntityRobot) {
				if (((TileEntityRobot) tile).getUuid().equals(controllerUuid)) {
					return tile;
				}
			}
		}
		return null;
	}

	@Override
	public float getEyeHeight() {
		return 2.125f;
	}

	@Override
	public Vec3 getLocation() {
		return worldObj.getWorldVec3Pool().getVecFromPool(posX, posY, posZ);
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

	/**
	 * Create a robot from a robot item. it copies the nbt across
	 * @param stack
	 * @return if it was made or not
	 */
	public boolean createFromItem(ItemStack stack) {

		/**
		 * This is where we'd read the available tags and decide which upgrades
		 * to apply
		 */
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = (NBTTagCompound) stack.getTagCompound().copy();
			inventory.readFromNBT(tag);
			controllerX = tag.getInteger("controllerX");
			controllerY = tag.getInteger("controllerY");
			controllerZ = tag.getInteger("controllerZ");
			controllerUuid = tag.getString("controllerUuid");
			robotId = tag.getInteger("robotId");
			TileEntityRobot controller = (TileEntityRobot) getController();
			if (controller == null) {
				return false;
			}
			controller.registerRobot(robotId, this);
			if (tag.hasKey("upgrades")) {
				upgradesNBT = tag.getCompoundTag("upgrades");
			}
			onInventoryChanged(inventory, 0);

		}
		return true;
	}

	public IRobotUpgradeInstance getInstanceForLuaMethod(String methodName) {
		return upgradeMethodMap.get(methodName);
	}

	@Override
	public EntityCreature getEntity() {
		return this;
	}

	@Override
	public void fireEvent(String eventName) {
		fireEvent(eventName, null);
	}

	// TODO: get controller and fire the event
	@Override
	public void fireEvent(String eventName, Object[] args) {
		TileEntity te = getController();
		if (te instanceof TileEntityRobot) {
			((TileEntityRobot)te).fireEvent(eventName, args);
		}
	}

	public void setDead() {
		TileEntityRobot controller = (TileEntityRobot) getController();
		if (controller != null) {
			controller.unregisterRobot(robotId);
		}
		super.setDead();
	}

	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		controllerX = tag.getInteger("controllerX");
		controllerY = tag.getInteger("controllerY");
		controllerZ = tag.getInteger("controllerZ");
		controllerUuid = tag.getString("controllerUuid");
		robotId = tag.getInteger("robotId");
		inventory.readFromNBT(tag);
		if (tag.hasKey("upgrades")) {
			upgradesNBT = tag.getCompoundTag("upgrades");
		}
		onInventoryChanged(inventory, 0);
	}

	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setInteger("controllerX", controllerX);
		tag.setInteger("controllerY", controllerY);
		tag.setInteger("controllerZ", controllerZ);
		tag.setString("controllerUuid", controllerUuid);
		tag.setInteger("robotId", robotId);
		inventory.writeToNBT(tag);
		NBTTagCompound upgrades = new NBTTagCompound();
		tag.setCompoundTag("upgrades", upgrades);
		for (Entry<String, IRobotUpgradeInstance> entry : upgradeInstances.entrySet()) {
			NBTTagCompound instanceTag = new NBTTagCompound();
			entry.getValue().writeToNBT(instanceTag);
			upgrades.setCompoundTag(entry.getKey(), instanceTag);
		}
	}

	/**
	 * If the player is sneaking and they click lets dismantle the robot TODO:
	 * only dismantle if owner or OP
	 */
	@Override
	public boolean interact(EntityPlayer player) {
		if (!worldObj.isRemote) {
			if (player.isSneaking()) {
				ItemStack robot = new ItemStack(OpenPeripheral.Items.robot);
				NBTTagCompound tag = new NBTTagCompound();
				this.writeEntityToNBT(tag);
				robot.setTagCompound(tag);
				setDead();
				BlockUtils.dropItemStackInWorld(worldObj, posX, posY, posZ, robot);
				return true;
			} else {
				player.openGui(OpenPeripheral.instance, OpenPeripheral.Gui.robotEntity.ordinal(), player.worldObj, entityId, 0, 0);
			}
		}

		return false;
	}
}

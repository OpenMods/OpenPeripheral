package openperipheral.common.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openperipheral.OpenPeripheral;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.api.RobotUpgradeManager;
import openperipheral.common.config.ConfigSettings;
import openperipheral.common.core.OPInventory;
import openperipheral.common.interfaces.IInventoryCallback;
import openperipheral.common.tileentity.TileEntityRobot;
import openperipheral.common.util.BlockUtils;
import openperipheral.common.util.InventoryUtils;

public class EntityRobot extends EntityCreature implements IRobot, IInventoryCallback {

	/**
	 * the location and id of the controller
	 */
	private int controllerX = 0;
	private int controllerY = 0;
	private int controllerZ = 0;
	private String controllerUuid = "[none]";
	private boolean linkedToController = false;
	private int robotId = 0;

	/**
	 * The weapon spin used for rendering clientside
	 */
	private float weaponSpin = 0.f;
	
	private float weaponSpinSpeed = 0.5f;
	
	private float fuelLevel = 0;
	
	/**
	 * The main inventory
	 */
	protected OPInventory inventory = new OPInventory("robot", false, 27);

	/**
	 * A map of currently installed instanceIds to module instance objects
	 */
	private HashMap<String, IRobotUpgradeInstance> upgradeInstances;
	
	/**
	 * map of method name to upgrade instance, so we know what object handles
	 * the called method
	 */
	private HashMap<String, IRobotUpgradeInstance> upgradeMethodMap;
	
	/**
	 * a map of upgrade instance id to upgrade AI objects. We need this
	 * to make sure we can remove any when the upgrade is disabled
	 */
	private HashMap<EntityAIBase, IRobotUpgradeInstance> upgradeAIMap;
	
	
	private NBTTagCompound upgradesNBT = new NBTTagCompound();
	
	public EntityRobot(World par1World) {
		super(par1World);
		upgradeMethodMap = new HashMap<String, IRobotUpgradeInstance>();
		upgradeInstances = new HashMap<String, IRobotUpgradeInstance>();
		upgradeAIMap = new HashMap<EntityAIBase, IRobotUpgradeInstance>();
		this.health = this.getMaxHealth();
		this.setSize(1F, 3F);
		this.moveSpeed = 0.22F;
		this.getNavigator().setAvoidsWater(true);
		inventory.addCallback(this);
		this.texture = String.format("%s/models/robot.png", ConfigSettings.TEXTURES_PATH);
	}

	@Override
	public float getMoveSpeed() {
		return moveSpeed;
	}

	@Override
	public void setMoveSpeed(float speed) {
		moveSpeed = speed;
	}
	
	@Override
	public float getWeaponSpinSpeed() {
		return weaponSpinSpeed;
	}

	@Override
	public void setWeaponSpinSpeed(float speed) {
		weaponSpinSpeed = speed;
	}
	
	@Override
	public void modifyWeaponSpinSpeed(float speed) {
		weaponSpinSpeed += speed;
	}
	
	/**
	 * Get the current fuel level
	 * @return the fuel level
	 */
	@Override
	public float getFuelLevel() {
		return fuelLevel;
	}

	/**
	 * set the fuel level
	 * @param fuel
	 */
	@Override
	public void setFuelLevel(float fuel) {
		fuelLevel = fuel;
	}
	
	/**
	 * add or remove fuel
	 * @param fuel
	 */
	@Override
	public void modifyFuelLevel(float fuel) {
		fuelLevel += fuel;
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
		
		ArrayList<String> validInstances = new ArrayList<String>();
		
		for (int i = 0; i < inventory.getSizeInventory() - 1; i++) {

			ItemStack stack = inventory.getStackInSlot(i);
			IRobotUpgradeProvider provider = RobotUpgradeManager.getProviderForStack(stack);
			
			if (provider == null) {
				continue;
			}
			
			String providerId = provider.getUpgradeId();
			
			validInstances.add(providerId);
			
			if (!upgradeInstances.containsKey(providerId)) {
				
				// create a new instance
				IRobotUpgradeInstance instance = provider.provideUpgradeInstance(this);
				
				// check if there's any information stored in our upgrades nbt
				if (upgradesNBT.hasKey(providerId)) {
					
					// let it read the nbt tag
					instance.readFromNBT(upgradesNBT.getCompoundTag(providerId));
				}
				
				// add all the methods to our method map
				for (IRobotMethod method : provider.getMethods()) {
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
			
			}
			
		}
		// remove any instances that are no longer valid
		for (String instanceKey : upgradeInstances.keySet()) {
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
				upgradeInstances.remove(instanceKey);
			}
		}
		
	}
	
	@Override
	public MovingObjectPosition getLookingAt() {
		Vec3 posVec = getLocation();
		posVec.yCoord += getEyeHeight();
        Vec3 lookVec = getLook(1.0f);
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

	//TODO: move to inventory module
	public void suckUp() {
		List<EntityItem> entities = worldObj.getEntitiesWithinAABB(EntityItem.class,
				AxisAlignedBB.getAABBPool().getAABB(posX - 2, posY - 2, posZ - 2, posX + 3, posY + 3, posZ + 3));

		for (EntityItem entity : entities) {

			if (entity.isDead) {
				continue;
			}

			ItemStack stack = entity.getEntityItem();

			if (stack != null) {

				InventoryUtils.insertItemIntoInventory(inventory, stack);
				if (stack.stackSize == 0) {
					entity.setDead();
				}
			}
		}
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
			
		} else {
			this.weaponSpin += getWeaponSpinSpeed();
		}
	}

	// TODO: add this as a packed boolean in the dataWatcher.
	// lets change the model if he's not linked up!
	@Override
	public boolean isLinkedToController() {
		return linkedToController;
	}

	protected void updateAITasks() {
		//temporarily store the pitch, because the super method sets it to 0
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

	@Override
	public int getMaxHealth() {
		return 40;
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

	public float getWeaponSpin() {
		return weaponSpin;
	}

	@Override
	public float getEyeHeight() {
		return 2.2f;
	}

	@Override
	public Vec3 getLocation() {
		return worldObj.getWorldVec3Pool().getVecFromPool(posX, posY, posZ);
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

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
	 * If the player is sneaking and they click lets dismantle the robot
	 * TODO: only dismantle if owner or OP
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
			}else {
				player.openGui(OpenPeripheral.instance, OpenPeripheral.Gui.robotEntity.ordinal(), player.worldObj, entityId, 0, 0);
			}
		}
		
		return false;
	}
}

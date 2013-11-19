package openperipheral.robots.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openperipheral.core.OPInventory;
import openperipheral.core.interfaces.IAttachable;
import openperipheral.core.interfaces.IConditionalSlots;
import openperipheral.core.interfaces.IHasSyncedGui;
import openperipheral.core.interfaces.IInventoryCallback;
import openperipheral.core.interfaces.IPeripheralProvider;
import openperipheral.core.util.GuiValueHolder;
import openperipheral.core.util.MiscUtils;
import openperipheral.robots.RobotPeripheral;
import openperipheral.robots.entity.EntityRobot;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;

public class TileEntityRobot extends TileEntity implements IPeripheralProvider, IHasSyncedGui, IAttachable, IConditionalSlots, ISidedInventory, IInventory, IInventoryCallback {

	private IHostedPeripheral peripheral = null;

	/**
	 * A map of robot ids to minecraft entitiy ids of currently connected robots
	 */
	private HashMap<Integer, Integer> robots = new HashMap<Integer, Integer>();

	/**
	 * A list of computers currently connected to this peripheral
	 */
	private List<IComputerAccess> computers = new ArrayList<IComputerAccess>();

	public static final int ROBOT_ITEM_SLOT = 0;

	protected GuiValueHolder guiValues = new GuiValueHolder();

	/**
	 * not really used yet
	 */
	private boolean hasActionInProgress = false;

	/**
	 * The rotation of the 'preview' robot rendered on the tile entity
	 */
	private float renderRot = 0;

	/**
	 * The inventory for upgrades and fuel
	 */
	private OPInventory inventory = new OPInventory("robottile", false, 1);

	/**
	 * A uuid is kept to make sure that this is a unique instance of this tile
	 * entity x, y, z is not strong enough, because if someone destroys the tile
	 * and re-places it, we need to make sure that any robots pointing to this
	 * tile are no longer valid
	 */
	private String uuid = UUID.randomUUID().toString();

	/**
	 * Unique robot id incrementor
	 */
	private int robotIdCounter = 0;

	/**
	 * The slot positions for the gui. {x, y}, {x, y}
	 */
	public static final int[] SLOTS = new int[] { 80, 34 };

	public TileEntityRobot() {
		inventory.addCallback(this);
	}

	/**
	 * == Other methods & interface implementations ==
	 */

	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote) {
			renderRot += 5.0f;
		}
	}

	public String getUuid() {
		return uuid;
	}

	public EntityRobot getRobotById(int id) throws Exception {
		if (!robots.containsKey(id)) { throw new Exception("Unable to find a robot using ID " + id); }
		int entityId = robots.get(id);
		Entity entity = worldObj.getEntityByID(entityId);
		if (entity == null || !(entity instanceof EntityRobot)) {
			robots.remove(id);
			throw new Exception("Unable to find a robot using ID " + id);
		}
		return (EntityRobot)entity;
	}

	public Integer[] getRobotIds() {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (Entry<Integer, Integer> entry : robots.entrySet()) {
			Entity entity = worldObj.getEntityByID(entry.getValue());
			if (entity != null && entity instanceof EntityRobot) {
				ids.add(entry.getKey());
			}
		}
		return ids.toArray(new Integer[ids.size()]);
	}

	/**
	 * Send an event to all connected computers
	 * 
	 * @param eventName
	 * @param args
	 */
	public void fireEvent(String eventName, Object... args) {
		if (args == null) {
			args = new Object[0];
		}
		for (IComputerAccess computer : computers) {
			args = MiscUtils.append(args, computer.getAttachmentName());
			computer.queueEvent(eventName, args);
		}
	}

	@Override
	public void addComputer(IComputerAccess computer) {
		computers.add(computer);
	}

	@Override
	public void removeComputer(IComputerAccess computer) {
		computers.remove(computer);
	}

	public float getRenderRot() {
		return renderRot;
	}

	/**
	 * Called whenever the inventory changes
	 */
	@Override
	public void onInventoryChanged(IInventory inventory, int slotNumber) {
		if (worldObj.isRemote) { return; }

		// if there's a robot in the slot, re-link it to this controller
		ItemStack robotStack = inventory.getStackInSlot(ROBOT_ITEM_SLOT);
		if (robotStack != null) {
			NBTTagCompound tag = robotStack.getTagCompound();
			if (tag == null) {
				tag = new NBTTagCompound();
				robotStack.setTagCompound(tag);
			}
			String currentUuid = tag.hasKey("controllerUuid")? tag.getString("controllerUuid") : null;
			if (!uuid.equals(currentUuid)) {
				tag.setString("controllerUuid", uuid);
				tag.setInteger("robotId", robotIdCounter++);
			}

			tag.setInteger("controllerX", xCoord);
			tag.setInteger("controllerY", yCoord);
			tag.setInteger("controllerZ", zCoord);
		}
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return inventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return inventory.getInvName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return inventory.isInvNameLocalized();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return inventory.getAccessibleSlotsFromSide(var1);
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return inventory.canInsertItem(i, itemstack, j);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return inventory.canExtractItem(i, itemstack, j);
	}

	@Override
	public boolean isValidForSlot(int i, ItemStack itemstack) {
		return isItemValidForSlot(i, itemstack);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canTakeStack(int slotNumber, EntityPlayer player) {
		return true;
	}

	@Override
	public int getGuiValue(int index) {
		return guiValues.get(index).getValue();
	}

	@Override
	public int[] getGuiValues() {
		return guiValues.asIntArray();
	}

	@Override
	public void onClientButtonClicked(int button) {}

	@Override
	public void onServerButtonClicked(EntityPlayer player, int button) {}

	@Override
	public void setGuiValue(int i, int value) {
		guiValues.get(i).setValue(value);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);
		tag.setInteger("robotIdCounter", robotIdCounter);
		tag.setString("uuid", uuid);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
		robotIdCounter = tag.getInteger("robotIdCounter");
		uuid = tag.getString("uuid");
	}

	@Override
	public IHostedPeripheral providePeripheral(World worldObj) {
		if (peripheral == null) {
			peripheral = new RobotPeripheral(this, worldObj);
		}
		return peripheral;
	}

	public boolean registerRobot(int robotId, EntityRobot entityRobot) {
		if (robots.containsKey(robotId)) {
			int currentRobotId = robots.get(robotId);
			Entity currentRobot = worldObj.getEntityByID(currentRobotId);
			if (currentRobot != null && currentRobot != entityRobot && !currentRobot.isDead) {
				entityRobot.setDead();
			}
			return false;
		}
		robots.put(robotId, entityRobot.entityId);
		return true;
	}

	public void unregisterRobot(int robotId) {
		robots.remove(robotId);
	}

}

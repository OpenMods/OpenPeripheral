package openperipheral.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.api.IConditionalSlots;
import openperipheral.api.IInventoryCallback;
import openperipheral.common.core.OPInventory;
import openperipheral.common.util.BlockUtils;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntityTicketMachine extends TileEntity implements IInventory, ISidedInventory, IInventoryCallback, IConditionalSlots {

	public static final int[] SLOTS = new int[] { 38, 34, 62, 34, 119, 34 };

	protected OPInventory inventory = new OPInventory("ticketmachine", false, 3);
	private Item ticketItem;
	private boolean isLocked = false;

	public TileEntityTicketMachine() {
		inventory.addCallback(this);
		ItemStack ticketStack = GameRegistry.findItemStack("Railcraft", "routing.ticket", 1);
		if (ticketStack != null) {
			ticketItem = ticketStack.getItem();
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
	public void openChest() {
		inventory.openChest();
	}

	@Override
	public void closeChest() {
		inventory.closeChest();
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		if (itemstack == null) {
			return false;
		}
		if (i == 2) {
			return false;
		}
		Item item = itemstack.getItem();
		if (i == 0 && item == Item.paper) {
			return true;
		}
		if (i == 1 && item == Item.dyePowder) {
			return true;
		}
		return false;
	}

	public boolean createTicket(String destination, boolean drop) {
		ItemStack paperStack = inventory.getStackInSlot(0);
		ItemStack inkStack = inventory.getStackInSlot(1);
		ItemStack outputStack = inventory.getStackInSlot(2);
		try {
			if (isStackValidForSlot(0, paperStack) && isStackValidForSlot(1, inkStack) && outputStack == null) {
				ItemStack output = new ItemStack(ticketItem);
				NBTTagCompound tag = new NBTTagCompound();
				tag.setString("owner", "Mikeemoo");
				tag.setString("dest", destination);
				output.setTagCompound(tag);
				decrStackSize(0, 1);
				decrStackSize(1, 1);
				if (drop) {
					BlockUtils.dropItemStackInWorld(worldObj, xCoord, yCoord, zCoord, output);
				} else {
					setInventorySlotContents(2, output);
				}
				return true;

			}
		} catch (Exception e) {
		}
		return false;
	}

	public void addBlockEvent(int eventId, int eventParam) {
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType().blockID, eventId, eventParam);
	}

	public void onBlockEventReceived(int eventId, int eventParam) {
		if (worldObj.isRemote) {
			if (eventId == 0) {
				isLocked = eventParam == 1;
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setBoolean("locked", isLocked);
		inventory.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		isLocked = tag.getBoolean("locked");
		inventory.readFromNBT(tag);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] { 0, 1, 2 };
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return isStackValidForSlot(i, itemstack);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return i == 2;
	}

	@Override
	public void onInventoryChanged(IInventory inventory) {

	}

	@Override
	public boolean canTakeStack(int slotNumber, EntityPlayer player) {
		return slotNumber == 2 || !isLocked;
	}
	
	public ForgeDirection getOrientation() {
		return ForgeDirection.getOrientation(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
	}

	public void lock() {
		isLocked = true;
		if (!worldObj.isRemote) {
			addBlockEvent(0, 1);
		}
	}

	public void unlock() {
		isLocked = false;
		if (!worldObj.isRemote) {
			addBlockEvent(0, 0);
		}
	}

	public boolean isLocked() {
		return isLocked;
	}

}

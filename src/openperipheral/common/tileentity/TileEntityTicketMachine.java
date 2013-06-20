package openperipheral.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
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
	private boolean hasTicket = false;
	private boolean isLocked = false;
	private String owner = "TicketMachine";

	public TileEntityTicketMachine() {
		inventory.addCallback(this);
		ItemStack ticketStack = GameRegistry.findItemStack("Railcraft", "routing.ticket", 1);
		if (ticketStack != null) {
			ticketItem = ticketStack.getItem();
		}
	}

	public void setOwner(String owner) {
		this.owner = owner;
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

	public boolean createTicket(String destination) {
		ItemStack paperStack = inventory.getStackInSlot(0);
		ItemStack inkStack = inventory.getStackInSlot(1);
		ItemStack outputStack = inventory.getStackInSlot(2);
		try {
			if (isStackValidForSlot(0, paperStack) && isStackValidForSlot(1, inkStack) && outputStack == null) {
				ItemStack output = new ItemStack(ticketItem);
				NBTTagCompound tag = new NBTTagCompound();
				tag.setString("owner", owner);
				tag.setString("dest", destination);
				output.setTagCompound(tag);
				decrStackSize(0, 1);
				decrStackSize(1, 1);
				setInventorySlotContents(2, output);
				worldObj.playSoundEffect((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D, "openperipheral.ticketmachine", 0.3F, 0.6F);
				
				return true;

			}
		} catch (Exception e) {
		}
		return false;
	}
	
	public boolean hasTicket() {
		return hasTicket;
	}
	
	@Override
	public Packet getDescriptionPacket() {
		Packet132TileEntityData packet = new Packet132TileEntityData();
		packet.actionType = 0;
		packet.xPosition = xCoord;
		packet.yPosition = yCoord;
		packet.zPosition = zCoord;
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNetwork(nbt);
		packet.customParam1 = nbt;
		return packet;
	}
	

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNetwork(pkt.customParam1);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	public void readFromNetwork(NBTTagCompound tag) {
		hasTicket = tag.getBoolean("hasTicket");
	}

	public void writeToNetwork(NBTTagCompound tag) {
		tag.setBoolean("hasTicket", hasTicket);
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
		tag.setString("owner", owner);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		isLocked = tag.getBoolean("locked");
		inventory.readFromNBT(tag);
		if (tag.hasKey("owner")) {
			owner = tag.getString("owner");
		}
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
		if (!worldObj.isRemote) {
			boolean nowHasTicket = inventory.getStackInSlot(2) != null;
			if (nowHasTicket != hasTicket) {
				hasTicket = nowHasTicket;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
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

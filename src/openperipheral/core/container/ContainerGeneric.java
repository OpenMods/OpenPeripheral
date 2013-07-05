package openperipheral.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import openperipheral.core.interfaces.IHasSyncedGui;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerGeneric extends Container {

	protected int inventorySize;
	protected IInventory inventory;
	protected IInventory playerInventory;

	protected int[] craftingProgress = new int[16];

	public ContainerGeneric(IInventory playerInventory, IInventory inventory, int[] slots) {
		this.inventorySize = inventory.getSizeInventory();
		this.playerInventory = playerInventory;
		this.inventory = inventory;
		addInventorySlots(slots);
		addPlayerInventorySlots();
	}
	
	protected void addInventorySlots(int[] slots) {
		for (int i = 0, slotId = 0; i < slots.length; i += 2, slotId++) {
			addSlotToContainer(new ConditionalSlot(inventory, slotId, slots[i], slots[i + 1]));
		}
	}
	
	protected void addPlayerInventorySlots() {
		for (int l = 0; l < 3; l++) {
			for (int k1 = 0; k1 < 9; k1++) {
				addSlotToContainer(new Slot(playerInventory, k1 + l * 9 + 9, 8 + k1 * 18, 84 + l * 18));
			}
		}

		for (int i1 = 0; i1 < 9; i1++) {
			addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting crafting) {
		super.addCraftingToCrafters(crafting);
		if (inventory instanceof IHasSyncedGui) {
			int[] craftingValues = ((IHasSyncedGui) inventory).getGuiValues();
			for (int i = 0; i < craftingValues.length; i++) {
				craftingProgress[i] = craftingValues[i];
				crafting.sendProgressBarUpdate(this, i, craftingValues[i]);
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (inventory instanceof IHasSyncedGui) {

			int[] newValues = ((IHasSyncedGui) inventory).getGuiValues();

			for (int i = 0; i < this.crafters.size(); ++i) {
				ICrafting icrafting = (ICrafting) this.crafters.get(i);

				for (int j = 0; j < newValues.length; j++) {

					if (craftingProgress[j] != newValues[j]) {

						icrafting.sendProgressBarUpdate(this, j, newValues[j]);

					}
				}
			}

			for (int i = 0; i < newValues.length; i++) {
				craftingProgress[i] = newValues[i];
			}
		}
	}

	public boolean enchantItem(EntityPlayer player, int button) {
		if (inventory instanceof IHasSyncedGui) {
			((IHasSyncedGui) inventory).onServerButtonClicked(player, button);
		}
		return false;
	}

	public int getInventorySize() {
		return inventorySize;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer pl, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (i < inventorySize) {
				if (!mergeItemStack(itemstack1, inventorySize, inventorySlots.size(), true))
					return null;
			} else if (!mergeItemStack(itemstack1, 0, inventorySize, false))
				return null;
			if (itemstack1.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}
		}
		return itemstack;
	}

	protected boolean mergeItemStack(ItemStack par1ItemStack, int par2, int par3, boolean par4) {
		boolean flag1 = false;
		int k = par2;

		if (par4) {
			k = par3 - 1;
		}

		Slot slot;
		ItemStack itemstack1;

		if (par1ItemStack.isStackable()) {
			while (par1ItemStack.stackSize > 0 && (!par4 && k < par3 || par4 && k >= par2)) {
				slot = (Slot) this.inventorySlots.get(k);
				itemstack1 = slot.getStack();
				boolean valid = slot.isItemValid(par1ItemStack);
				if (valid && itemstack1 != null && itemstack1.itemID == par1ItemStack.itemID
						&& (!par1ItemStack.getHasSubtypes() || par1ItemStack.getItemDamage() == itemstack1.getItemDamage())
						&& ItemStack.areItemStackTagsEqual(par1ItemStack, itemstack1)) {
					int l = itemstack1.stackSize + par1ItemStack.stackSize;

					if (l <= par1ItemStack.getMaxStackSize()) {
						par1ItemStack.stackSize = 0;
						itemstack1.stackSize = l;
						slot.onSlotChanged();
						flag1 = true;
					} else if (itemstack1.stackSize < par1ItemStack.getMaxStackSize()) {
						par1ItemStack.stackSize -= par1ItemStack.getMaxStackSize() - itemstack1.stackSize;
						itemstack1.stackSize = par1ItemStack.getMaxStackSize();
						slot.onSlotChanged();
						flag1 = true;
					}
				}

				if (par4) {
					--k;
				} else {
					++k;
				}
			}
		}
		if (par1ItemStack.stackSize > 0) {
			if (par4) {
				k = par3 - 1;
			} else {
				k = par2;
			}
			while (!par4 && k < par3 || par4 && k >= par2) {
				slot = (Slot) this.inventorySlots.get(k);
				itemstack1 = slot.getStack();
				boolean valid = slot.isItemValid(par1ItemStack);
				if (valid && itemstack1 == null) {
					slot.putStack(par1ItemStack.copy());
					slot.onSlotChanged();
					par1ItemStack.stackSize = 0;
					flag1 = true;
					break;
				}

				if (par4) {
					--k;
				} else {
					++k;
				}
			}
		}

		return flag1;
	}

	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2) {
		if (inventory instanceof IHasSyncedGui) {
			((IHasSyncedGui) inventory).setGuiValue(par1, par2);
		}
	}
}
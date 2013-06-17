package openperipheral.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

public class TileEntityPlayerInventory extends TileEntity implements IInventory {

	private EntityPlayer player;
	
	@Override
	public int getSizeInventory() {
		if (player != null) {
			return player.inventory.getSizeInventory();
		}
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (player != null) {
			return player.inventory.getStackInSlot(i);
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (player != null) {
			return player.inventory.decrStackSize(i, j);
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (player != null) {
			return player.inventory.getStackInSlotOnClosing(i);
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (player != null) {
			player.inventory.setInventorySlotContents(i, itemstack);
		}
	}

	@Override
	public String getInvName() {
		if (player != null) {
			return player.inventory.getInvName();
		}
		return "EmptyInventory";
	}

	@Override
	public boolean isInvNameLocalized() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		if (player != null) {
			return player.inventory.getInventoryStackLimit();
		}
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openChest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeChest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		if (player != null) {
			return player.inventory.isStackValidForSlot(i, itemstack);
		}
		return false;
	}

	public EntityPlayer getPlayer() {
		return player;
	}
	
	public boolean hasPlayer() {
		if (worldObj == null) return false;
		return worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 1;
	}
	
	public void setPlayer(EntityPlayer p) {
		player = p;
        worldObj.playSoundEffect((double)xCoord + 0.5D, (double)yCoord + 0.1D, (double)zCoord + 0.5D, "random.click", 0.3F, 0.6F);
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, p == null ? 0 : 1, 3);
	}
	
	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			EntityPlayer player = getPlayer();
			if (player != null) {
				ChunkCoordinates coordinates = player.getPlayerCoordinates();
				if (coordinates.posX != xCoord || 
					coordinates.posY != yCoord || 
					coordinates.posZ != zCoord) {
					setPlayer(null);
				}
			}
		}
	}

}

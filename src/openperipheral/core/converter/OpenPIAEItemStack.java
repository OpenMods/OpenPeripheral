package openperipheral.core.converter;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import appeng.api.IAEItemStack;
import appeng.api.IAETagCompound;

public class OpenPIAEItemStack implements IAEItemStack, Comparable<IAEItemStack> {

	private int itemId;
	private long qty;
	private int dmg;
	private boolean craftable;
	
	public OpenPIAEItemStack(int itemId, int dmg, long qty) {
		this.itemId = itemId;
		this.qty = qty;
		this.dmg = dmg;
	}
	
	@Override
	public int getItemID() {
		return itemId;
	}

	@Override
	public int getItemDamage() {
		return dmg;
	}

	@Override
	public IAETagCompound getTagCompound() {
		return null;
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(itemId, dmg, (int) qty);
	}

	@Override
	public IAEItemStack copy() {
		return new OpenPIAEItemStack(itemId, dmg, qty);
	}

	@Override
	public long getStackSize() {
		return qty;
	}

	@Override
	public void setStackSize(long stackSize) {
		this.qty = stackSize;

	}

	@Override
	public long getCountRequestable() {
		return qty;
	}

	@Override
	public void setCountRequestable(long countRequestable) {
		setStackSize(countRequestable);
	}

	@Override
	public boolean isCraftable() {
		return craftable;
	}

	@Override
	public void setCraftable(boolean isCraftable) {
		craftable = isCraftable;
	}

	@Override
	public int getDef() {
		return this.itemId;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isMeaninful() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasTagCompound() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void add(IAEItemStack option) {
		// TODO Auto-generated method stub

	}

	@Override
	public void incStackSize(long i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void decStackSize(long i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void incCountRequestable(long i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void decCountRequestable(long i) {
		// TODO Auto-generated method stub

	}

	@Override
	public Item getItem() {
		return Item.itemsList[itemId];
	}

	@Override
	public void writeToNBT(NBTTagCompound i) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean sameOre(Object oreID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int compareTo(IAEItemStack o) {
		return 0;
	}

}

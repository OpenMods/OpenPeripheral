package openperipheral.common.item.meta;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import openperipheral.OpenPeripheral;
import openperipheral.common.interfaces.IMetaItem;
import openperipheral.common.item.ItemGeneric;

public class MetaSilislimeRubber implements IMetaItem {

	private Icon icon;
	
	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "openperipheral.silislimerubber";
	}

	@Override
	public boolean hitEntity(ItemStack itemStack, EntityLiving target, EntityLiving player) {
		return false;
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, EntityPlayer player, World world) {
		return itemStack;
	}

	@Override
	public boolean displayInCreative() {
		return true;
	}

	@Override
	public void registerIcons(IconRegister register) {
		icon = register.registerIcon("openperipheral:silislimerubber");
	}

	@Override
	public void addRecipe() {
		FurnaceRecipes.smelting().addSmelting(Item.slimeBall.itemID, 0, ItemGeneric.Metas.silislime.newItemStack(), 0.5f);
	}

}

package openperipheral.common.item.meta;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import openperipheral.common.interfaces.IMetaItem;
import openperipheral.common.item.ItemGeneric;

public class MetaCoiledWire implements IMetaItem {

	private Icon icon;
	
	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "openperipheral.coiledwire";
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
		icon = register.registerIcon("openperipheral:coiledwire");
	}

	@Override
	public void addRecipe() {
		CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(
				ItemGeneric.Metas.coiledWire.newItemStack(),
				ItemGeneric.Metas.thinWire.newItemStack()
		));
	}

}

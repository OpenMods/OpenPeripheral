package openperipheral.core.item.meta;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import openperipheral.core.interfaces.IMetaItem;
import openperipheral.core.item.ItemGeneric;

public class MetaSolarCell implements IMetaItem {

	private Icon icon;

	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "openperipheral.solarcell";
	}

	@Override
	public boolean hitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase player) {
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
		icon = register.registerIcon("openperipheral:solarcell");
	}

	@Override
	public void addRecipe() {
		CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(
				ItemGeneric.Metas.solarCell.newItemStack(9),
				new ItemStack(Block.daylightSensor)
		));
		CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(
				new ItemStack(Block.daylightSensor),
				ItemGeneric.Metas.solarCell.newItemStack(),
				ItemGeneric.Metas.solarCell.newItemStack(),
				ItemGeneric.Metas.solarCell.newItemStack(),
				ItemGeneric.Metas.solarCell.newItemStack(),
				ItemGeneric.Metas.solarCell.newItemStack(),
				ItemGeneric.Metas.solarCell.newItemStack(),
				ItemGeneric.Metas.solarCell.newItemStack(),
				ItemGeneric.Metas.solarCell.newItemStack(),
				ItemGeneric.Metas.solarCell.newItemStack()
		));
	}

}

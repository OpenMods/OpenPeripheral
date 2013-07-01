package openperipheral.common.item.meta;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import openperipheral.OpenPeripheral;
import openperipheral.api.IMetaItem;
import openperipheral.common.item.ItemGeneric;

public class MetaThinWire implements IMetaItem {

	private Icon icon;
	
	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "openperipheral.thinwire";
	}

	@Override
	public boolean hitEntity(ItemStack itemStack, EntityLiving target, EntityLiving player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		// TODO Auto-generated method stub
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
		icon = register.registerIcon("openperipheral:thinwire");
	}

	@Override
	public void addRecipe() {
		CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(
				ItemGeneric.Metas.ribbonCable.newItemStack(),
				new Object[] {
					"i",
					"r",
					"i",
					Character.valueOf('r'), new ItemStack(Item.redstone),
					Character.valueOf('i'), new ItemStack(Item.ingotIron),			
				}
		));
		CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(
				ItemGeneric.Metas.ribbonCable.newItemStack(),
				new Object[] {
					"iri",
					Character.valueOf('r'), new ItemStack(Item.redstone),
					Character.valueOf('i'), new ItemStack(Item.ingotIron),			
				}
		));
		CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(
				ItemGeneric.Metas.thinWire.newItemStack(),
				ItemGeneric.Metas.coiledWire.newItemStack()
		));
		CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(
				ItemGeneric.Metas.thinWire.newItemStack(9),
				ItemGeneric.Metas.ribbonCable.newItemStack()
		));
	}

}

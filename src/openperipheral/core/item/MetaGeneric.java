package openperipheral.core.item;

import java.lang.reflect.Array;
import java.util.Arrays;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import openperipheral.OpenPeripheral;
import openperipheral.core.interfaces.IMetaItem;
import openperipheral.core.item.ItemGeneric.Metas;

public class MetaGeneric implements IMetaItem {

	private String name;
	private Icon icon;
	private Object[][] recipes;

	public MetaGeneric(String name, Object[]... recipe) {
		this.name = name;
		this.recipes = recipe;
	}

	public MetaGeneric(String name, int amount, Object... recipe) {
		this.name = name;
		recipe = prepend(recipe, amount);
		this.recipes = new Object[][] { recipe };
	}

	/** temp hack from mikee **/
	public static Object[] prepend(Object[] oldArray, Object o) {

		Object[] newArray = (Object[]) Array.newInstance(oldArray.getClass()
				.getComponentType(), oldArray.length + 1);
		System.arraycopy(oldArray, 0, newArray, 1, oldArray.length);
		newArray[0] = o;
		return newArray;
	}

	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return String.format("openperipheral.%s", name);
	}

	@Override
	public boolean hitEntity(ItemStack itemStack, EntityLivingBase target,
			EntityLivingBase player) {
		return false;
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player,
			World world, int x, int y, int z, int side, float par8, float par9,
			float par10) {
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, EntityPlayer player,
			World world) {
		return itemStack;
	}

	@Override
	public boolean displayInCreative() {
		return true;
	}

	@Override
	public void registerIcons(IconRegister register) {
		icon = register.registerIcon(String.format("openperipheral:%s", name));
	}

	@Override
	public void addRecipe() {
		if (recipes == null)
			return;
		for (int i = 0; i < recipes.length; i++) {
			Object[] recipe = recipes[i];
			int amount = (Integer) recipe[0];
			boolean smelting = false;
			int itemId = 0;
			int itemMeta = 0;
			if (recipe[1] instanceof Integer) {
				itemId = amount;
				itemMeta = (Integer) recipe[1];
				smelting = true;
			} else {
				recipe = Arrays.copyOfRange(recipe, 1, recipe.length);
			}
			for (int j = 0; j < recipe.length; j++) {
				if (recipe[j] instanceof Metas) {
					recipe[j] = ((Metas) recipe[j]).newItemStack();
				}
			}
			IRecipe r = null;
			if (smelting) {
				FurnaceRecipes.smelting().addSmelting(itemId, itemMeta,
						(ItemStack) recipe[2], (Float) recipe[3]);
			} else {
				if (recipe[0] instanceof String) {
					r = new ShapedOreRecipe(
							OpenPeripheral.Items.generic.newItemStack(this,
									amount), recipe);
				} else {
					r = new ShapelessOreRecipe(
							OpenPeripheral.Items.generic.newItemStack(this,
									amount), recipe);
				}
				if (r != null) {
					CraftingManager.getInstance().getRecipeList().add(r);
				}
			}
		}
	}

}

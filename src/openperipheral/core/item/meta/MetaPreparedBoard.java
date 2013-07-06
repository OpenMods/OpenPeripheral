package openperipheral.core.item.meta;

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

public class MetaPreparedBoard implements IMetaItem {

	private Icon icon;

	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "openperipheral.preparedboard";
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
		icon = register.registerIcon("openperipheral:preparedboard");
	}

	@Override
	public void addRecipe() {
		CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(
				ItemGeneric.Metas.preparedPCB.newItemStack(),
				ItemGeneric.Metas.pcb.newItemStack(),
				ItemGeneric.Metas.resistor.newItemStack(),
				ItemGeneric.Metas.transistor.newItemStack(),
				ItemGeneric.Metas.led.newItemStack(),
				ItemGeneric.Metas.capacitor.newItemStack(),
				ItemGeneric.Metas.optoisolator.newItemStack(),
				ItemGeneric.Metas.thinWire.newItemStack(),
				ItemGeneric.Metas.microcontroller.newItemStack()
		));
	}

}

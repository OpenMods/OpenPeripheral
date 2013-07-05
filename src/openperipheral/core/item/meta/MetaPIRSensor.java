package openperipheral.core.item.meta;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import openperipheral.core.interfaces.IMetaItem;

public class MetaPIRSensor implements IMetaItem {

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean displayInCreative() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerIcons(IconRegister register) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRecipe() {
		// TODO Auto-generated method stub
		
	}

}

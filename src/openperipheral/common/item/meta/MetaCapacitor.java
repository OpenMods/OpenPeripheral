package openperipheral.common.item.meta;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openperipheral.api.IMetaItem;
import openperipheral.common.entity.EntityRobot;
import openperipheral.common.item.ItemGeneric;

public class MetaCapacitor implements IMetaItem {

	private Icon icon;
	
	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "openperipheral.capacitor";
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
		if (!world.isRemote) {
			EntityRobot npc = new EntityRobot(world);
			npc.setLocationAndAngles(player.posX, player.posY, player.posZ, 0, 0);
			world.spawnEntityInWorld(npc);
			System.out.println("spawned");
		}
		return itemStack;
	}

	@Override
	public boolean displayInCreative() {
		return true;
	}

	@Override
	public void registerIcons(IconRegister register) {
		icon = register.registerIcon("openperipheral:capacitor");
	}

	@Override
	public void addRecipe() {
		CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(
				ItemGeneric.Metas.capacitor.newItemStack(),
				new Object[] {
					"srs",
					"srs",
					"w w",
					Character.valueOf('s'), ItemGeneric.Metas.plasticSheet.newItemStack(),
					Character.valueOf('r'), ItemGeneric.Metas.silislime.newItemStack(),
					Character.valueOf('w'), ItemGeneric.Metas.thinWire.newItemStack(),
				}
		));
	}

}

package openperipheral.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openperipheral.OpenPeripheral;
import openperipheral.common.config.ConfigSettings;
import openperipheral.common.util.MiscUtils;
import openperipheral.common.util.RecipeUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGlasses extends ItemArmor {

	public ItemGlasses() {
		super(ConfigSettings.glassesId, EnumArmorMaterial.CHAIN, 0, 0);
		setMaxDamage(0);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(OpenPeripheral.tabOpenPeripheral);
		setUnlocalizedName("openperipheral.glasses");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs tab, List subItems) {
		subItems.add(new ItemStack(id, 1, 0));
		subItems.add(RecipeUtils.getGuideItemStack());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		if (itemStack.hasTagCompound()) {
			NBTTagCompound tag = itemStack.getTagCompound();
			if (tag.hasKey("openp")) {
				list.add("Key: " + tag.getCompoundTag("openp").getString("guid"));
			}
		}
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer) {
		switch (MiscUtils.getHoliday()) {
		case 1:
			return "/mods/openperipheral/textures/models/glasses_valentines.png";
		case 2:
			return "/mods/openperipheral/textures/models/glasses_halloween.png";
		case 3:
			return "/mods/openperipheral/textures/models/glasses_christmas.png";
		default:
			return "/mods/openperipheral/textures/models/glasses.png";
		}
	}

	@Override
	public void registerIcons(IconRegister register) {
		itemIcon = register.registerIcon("openperipheral:glasses");
	}

}

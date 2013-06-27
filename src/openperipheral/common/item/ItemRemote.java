package openperipheral.common.item;

import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import openperipheral.OpenPeripheral;
import openperipheral.common.config.ConfigSettings;
import openperipheral.common.util.RecipeUtils;
import openperipheral.common.util.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;

public class ItemRemote extends Item {

	public ItemRemote() {
		super(ConfigSettings.remoteId);
		setCreativeTab(OpenPeripheral.tabOpenPeripheral);
	}
	
	@Override
	public void registerIcons(IconRegister register) {
		itemIcon = register.registerIcon("openperipheral:remote");
	}
	
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
    	if (!par2World.isRemote) { 
    		par3EntityPlayer.openGui(OpenPeripheral.instance, OpenPeripheral.Gui.remote.ordinal(), par2World, 780, 4, -570);
    	}
        return par1ItemStack;
    }
    
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer player, World world, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        return false;
    }
}

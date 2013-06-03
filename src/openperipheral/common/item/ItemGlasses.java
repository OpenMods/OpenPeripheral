package openperipheral.common.item;

import java.util.List;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openperipheral.OpenPeripheral;
import openperipheral.common.terminal.DrawableManager;
import openperipheral.common.terminal.IDrawable;
import openperipheral.common.tileentity.TileEntityGlassesBridge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGlasses extends ItemArmor {

	public ItemGlasses() {
		super(OpenPeripheral.Config.glassesId, EnumArmorMaterial.CHAIN, 0, 0);
		setMaxDamage(0);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName("openperipheral.glasses");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player,
			List list, boolean par4) {
		if (itemStack.hasTagCompound()) {
			list.add("Key: " + itemStack.getTagCompound().getString("guid"));
		}
	}

	@Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer) {
		return "/mods/openperipheral/textures/models/glasses.png";
    }

	@Override
	public void registerIcons(IconRegister register) {
		itemIcon = register.registerIcon("openperipheral:glasses");
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
		if (!par3World.isRemote) {
	
			TileEntity clickedBlock = par3World.getBlockTileEntity(par4, par5, par6);
	        if (clickedBlock != null && clickedBlock instanceof TileEntityGlassesBridge) {
	        	TileEntityGlassesBridge bridge = (TileEntityGlassesBridge) clickedBlock;
	        	NBTTagCompound tag = new NBTTagCompound();
	        	tag.setString("guid", bridge.getGuid());
	        	tag.setInteger("x", bridge.xCoord);
	        	tag.setInteger("y", bridge.yCoord);
	        	tag.setInteger("z", bridge.zCoord);
	        	tag.setInteger("d", bridge.worldObj.provider.dimensionId);
	        	par1ItemStack.setTagCompound(tag);
	        }
		}
        return super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
    }

	public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack stack)
	{
		super.onArmorTickUpdate(world, player, stack);
		if (!world.isRemote) {
			TileEntityGlassesBridge bridge = getGlassesBridge(player.worldObj, stack);
			if (bridge != null) {
				bridge.registerPlayer(player);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void renderHelmetOverlay(ItemStack stack, EntityPlayer player, ScaledResolution resolution, float partialTicks, boolean hasScreen, int mouseX, int mouseY){
		DrawableManager manager = OpenPeripheral.instance.getDrawableManager();
		for (IDrawable drawable : manager.getDrawables()) {
			drawable.draw(stack, player, resolution, partialTicks, hasScreen, mouseX, mouseY);
		}
	}
	
	public TileEntityGlassesBridge getGlassesBridge(World worldObj, ItemStack stack) {
		if (stack.hasTagCompound()) {
			
			NBTTagCompound tag = stack.getTagCompound();
			
			int x = tag.getInteger("x");
			int y = tag.getInteger("y");
			int z = tag.getInteger("z");
			int d = tag.getInteger("d");
			
			if (d == worldObj.provider.dimensionId) {
				
				if (worldObj.blockExists(x, y, z)) {
					
					TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
					
					if (tile instanceof TileEntityGlassesBridge) {
						return (TileEntityGlassesBridge) tile;
					}
				}
			}
		}
		return null;
	}

}

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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;

public class ItemRemote extends Item {

	public Icon standard;
	public Icon advanced;
	
	public ItemRemote() {
		super(ConfigSettings.remoteId);
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(OpenPeripheral.tabOpenPeripheral);
		setUnlocalizedName("openperipheral.remote");
	}
	

	@Override
	public Icon getIconFromDamage(int i) {
		return i == 1 ? advanced : standard;
	}
	
	@Override
	public void registerIcons(IconRegister register) {
		advanced = register.registerIcon("openperipheral:remote");
		standard = register.registerIcon("openperipheral:remotestandard");
	}
	
	
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		TileEntity tile = getTileForStack(player.worldObj, stack);
		if (tile != null) {
			list.add(String.format("Linked to %s, %s, %s", tile.xCoord, tile.yCoord, tile.zCoord));
		}
	}

	public TileEntity getTileForStack(World world, ItemStack stack) {
		NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
    	NBTTagCompound ns = (NBTTagCompound) (tag.hasKey("openpRemote") ? tag.getTag("openpRemote") : new NBTTagCompound());
    	if (ns.hasKey("x") && ns.hasKey("y") && ns.hasKey("z")) {
    		int x = ns.getInteger("x");
    		int y = ns.getInteger("y");
    		int z = ns.getInteger("z");
    		if (ns.hasKey("dmg")) {
        		byte dmg = ns.getByte("dmg");
        		if (!world.isRemote) { 
        			stack.setItemDamage(dmg);
        		}
        		ns.removeTag("dmg");
    		}else {
	    		TileEntity tile = world.getBlockTileEntity(x, y, z);
	    		if (tile != null && tile.getClass().getName() == "dan200.computer.shared.TileEntityComputer") {
	    			return tile;
	    		}
    		}
    	}
    	return null;
	}
	
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer par3EntityPlayer)
    {
    	if (!world.isRemote) { 
    		TileEntity tile = getTileForStack(world, stack);
    		if (tile != null) {
    			par3EntityPlayer.openGui(OpenPeripheral.instance, OpenPeripheral.Gui.remote.ordinal(), world, tile.xCoord, tile.yCoord, tile.zCoord);
    		}
        }
        return stack;
    }
    
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile != null && tile.getClass().getName() == "dan200.computer.shared.TileEntityComputer") {
	    	NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
	    	NBTTagCompound ns = (NBTTagCompound) (tag.hasKey("openpRemote") ? tag.getTag("openpRemote") : new NBTTagCompound());
	    	ns.setInteger("x", x);
	    	ns.setInteger("y", y);
	    	ns.setInteger("z", z);
	    	ns.setByte("dmg", (byte)((world.getBlockMetadata(x, y, z) & 0x8) >> 3));
	    	System.out.println("DMG = "+ns.getByte("dmg"));
	    	tag.setTag("openpRemote", ns);
	    	if (!world.isRemote) {
	    		player.sendChatToPlayer("Linked remote terminal");
	    	}
	    	stack.setTagCompound(tag);
	    	return false;
		}
        return true;
    }
}

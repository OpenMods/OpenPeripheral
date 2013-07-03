package openperipheral.common.block;

import java.util.HashMap;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.OpenPeripheral;
import openperipheral.common.config.ConfigSettings;
import openperipheral.common.tileentity.TileEntityTicketMachine;
import openperipheral.common.util.BlockUtils;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockTicketMachine extends BlockContainer {

	public static class Icons {
		public static Icon top;
		public static Icon bottom;
		public static Icon side_left;
		public static Icon side_right;
		public static Icon front;
		public static Icon front_ticket;
		public static Icon back;
	}

	public HashMap<ForgeDirection, Icon[]> orientations = new HashMap<ForgeDirection, Icon[]>();

	public BlockTicketMachine() {
		super(ConfigSettings.ticketMachineId, Material.ground);
		setHardness(0.5F);
		setCreativeTab(OpenPeripheral.tabOpenPeripheral);
		GameRegistry.registerBlock(this, "ticketmachine");
		GameRegistry.registerTileEntity(TileEntityTicketMachine.class, "ticketmachine");
		setUnlocalizedName("openperipheral.ticketmachine");
	}

	@Override
	public void registerIcons(IconRegister register) {
		Icons.front = register.registerIcon("openperipheral:ticketmachine_front");
		Icons.front_ticket = register.registerIcon("openperipheral:ticketmachine_front_ticket");
		Icons.back = register.registerIcon("openperipheral:ticketmachine_back");
		Icons.side_left = register.registerIcon("openperipheral:ticketmachine_side_left");
		Icons.side_right = register.registerIcon("openperipheral:ticketmachine_side_right");
		Icons.top = register.registerIcon("openperipheral:ticketmachine_top");
		Icons.bottom = register.registerIcon("openperipheral:ticketmachine_bottom");

		orientations.put(ForgeDirection.WEST, new Icon[] { Icons.bottom, Icons.top, Icons.side_right, Icons.side_left, Icons.front, Icons.back });
		orientations.put(ForgeDirection.EAST, new Icon[] { Icons.bottom, Icons.top, Icons.side_left, Icons.side_right, Icons.back, Icons.front });
		orientations.put(ForgeDirection.NORTH, new Icon[] { Icons.bottom, Icons.top, Icons.front, Icons.back, Icons.side_left, Icons.side_right });
		orientations.put(ForgeDirection.SOUTH, new Icon[] { Icons.bottom, Icons.top, Icons.back, Icons.front, Icons.side_right, Icons.side_left });

	}
	
	@Override
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
        Icon ret = getIcon(side, blockAccess.getBlockMetadata(x, y, z));
        if (ret == Icons.front){
        	TileEntity te = blockAccess.getBlockTileEntity(x, y, z);
        	if (te instanceof TileEntityTicketMachine) {
        		if (((TileEntityTicketMachine)te).hasTicket()) {
        			ret = Icons.front_ticket;
        		}
        	}
        }
        
        return ret;
    }

	@Override
	public Icon getIcon(int side, int metadata) {
		ForgeDirection orientation = ForgeDirection.getOrientation(metadata);
		if (orientations.containsKey(orientation)) {
			return orientations.get(orientation)[side];
		}
		return orientations.get(ForgeDirection.WEST)[side];
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityTicketMachine();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (player.isSneaking() || tileEntity == null) {
			return false;
		}
		player.openGui(OpenPeripheral.instance, OpenPeripheral.Gui.ticketMachine.ordinal(), world, x, y, z);
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityliving, ItemStack itemStack) {
		super.onBlockPlacedBy(world, z, y, z, entityliving, itemStack);
		ForgeDirection orientation = BlockUtils.get2dOrientation(entityliving);
		world.setBlockMetadataWithNotify(x, y, z, orientation.getOpposite().ordinal(), 3);
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityTicketMachine && entityliving instanceof EntityPlayer) {
			((TileEntityTicketMachine)te).setOwner(((EntityPlayer)entityliving).username);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		BlockUtils.dropInventoryItems(world.getBlockTileEntity(x, y, z));
		super.breakBlock(world, x, y, z, par5, par6);
	}
	
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventId, int eventParam) {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof TileEntityTicketMachine) {
        	((TileEntityTicketMachine)te).onBlockEventReceived(eventId, eventParam);
        }
    	return true;
    }

	@Override
	public boolean canBeReplacedByLeaves(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return false;
	}
}

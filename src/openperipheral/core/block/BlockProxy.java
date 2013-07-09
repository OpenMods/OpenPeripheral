package openperipheral.core.block;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.OpenPeripheral;
import openperipheral.core.ConfigSettings;
import openperipheral.core.util.BlockUtils;
import openperipheral.core.util.ReflectionHelper;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockProxy extends BlockContainer {

	public static class Icons {
		public static Icon top;
		public static Icon bottom;
		public static Icon side_left;
		public static Icon side_right;
		public static Icon side_up;
		public static Icon side_down;
	}

	public HashMap<ForgeDirection, Icon[]> orientations = new HashMap<ForgeDirection, Icon[]>();

	public BlockProxy() {
		super(ConfigSettings.proxyBlockId, Material.ground);
		setHardness(0.5F);
		setCreativeTab(OpenPeripheral.tabOpenPeripheral);
		GameRegistry.registerBlock(this, "proxyblock");
		GameRegistry.registerTileEntity(TileEntityProxy.class, "proxyblock");
		setUnlocalizedName("openperipheral.proxyblock");
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemStack) {
		super.onBlockPlacedBy(world, z, y, z, entityliving, itemStack);
		ForgeDirection orientation = BlockUtils.get3dOrientation(entityliving);
		world.setBlockMetadataWithNotify(x, y, z, orientation.getOpposite().ordinal(), 3);
		refreshProxiedPeripheral(world, x, y, z);
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, int l) {
		refreshProxiedPeripheral(world, x, y, z);
	}

	public void refreshProxiedPeripheral(World world, int x, int y, int z) {

		TileEntity selfTe = world.getBlockTileEntity(x, y, z);
		int metaData = world.getBlockMetadata(x, y, z);
		ForgeDirection orientation = ForgeDirection.getOrientation(metaData);
		ForgeDirection behind = orientation.getOpposite();
		
		if (selfTe != null && selfTe instanceof TileEntityProxy) {
			TileEntity targetTe = world.getBlockTileEntity(x + behind.offsetX, y + behind.offsetY, z + behind.offsetZ);
			OpenPeripheral.peripheralHandler.invalidate(targetTe);
			OpenPeripheral.peripheralHandler.invalidate(selfTe);
			((TileEntityProxy) selfTe).setTarget(targetTe, orientation.ordinal());
			int cableX = x + orientation.offsetX;
			int cableY = y + orientation.offsetY;
			int cableZ = z + orientation.offsetZ;
			world.notifyBlockOfNeighborChange(cableX, cableY, cableZ, blockID);
			int cableBlockId = world.getBlockId(cableX, cableY, cableZ);
			Block cableBlock = Block.blocksList[cableBlockId];
			TileEntity cableTe = world.getBlockTileEntity(cableX, cableY, cableZ);
			try {
				// if you see this dan200, please be aware that it wasn't me that added this line
				// it may look like me, and I'm sure the git blame list says it was me
				// but... it's wrong. Wasn't me. Hey, look over there! Something shiny!
				ReflectionHelper.callMethod("", cableTe, new String[] { "networkChanged"});
			} catch (Exception e) {
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityProxy();
	}

	@Override
	public void registerIcons(IconRegister registry) {
		Icons.side_left = registry.registerIcon("openperipheral:proxy_side_left");
		Icons.side_right = registry.registerIcon("openperipheral:proxy_side_right");
		Icons.side_up = registry.registerIcon("openperipheral:proxy_side_up");
		Icons.side_down = registry.registerIcon("openperipheral:proxy_side_down");
		Icons.top = registry.registerIcon("openperipheral:proxy_top");
		Icons.bottom = registry.registerIcon("openperipheral:proxy_bottom");
		orientations.put(ForgeDirection.DOWN, new Icon[] { Icons.bottom, Icons.top, Icons.side_up, Icons.side_up, Icons.side_up, Icons.side_up });
		orientations.put(ForgeDirection.UP, new Icon[] { Icons.top, Icons.bottom, Icons.side_down, Icons.side_down, Icons.side_down, Icons.side_down });
		orientations.put(ForgeDirection.WEST, new Icon[] { Icons.side_right, Icons.side_right, Icons.side_left, Icons.side_right, Icons.bottom, Icons.top });
		orientations.put(ForgeDirection.EAST, new Icon[] { Icons.side_left, Icons.side_left, Icons.side_right, Icons.side_left, Icons.top, Icons.bottom });
		orientations.put(ForgeDirection.SOUTH, new Icon[] { Icons.side_up, Icons.side_up, Icons.top, Icons.bottom, Icons.side_left, Icons.side_right });
		orientations.put(ForgeDirection.NORTH, new Icon[] { Icons.side_down, Icons.side_down, Icons.bottom, Icons.top, Icons.side_right, Icons.side_left });
	}

	@Override
	public Icon getIcon(int side, int metadata) {
		ForgeDirection orientation = ForgeDirection.getOrientation(metadata);
		if (orientations.containsKey(orientation)) {
			return orientations.get(orientation)[side];
		}
		return Icons.side_left;
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

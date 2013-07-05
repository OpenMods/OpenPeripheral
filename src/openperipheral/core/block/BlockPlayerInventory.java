package openperipheral.core.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.OpenPeripheral;
import openperipheral.core.ConfigSettings;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockPlayerInventory extends BlockContainer {

	public BlockPlayerInventory() {
		super(ConfigSettings.playerInventoryId, Material.ground);
		setHardness(0.5F);
		setStepSound(soundMetalFootstep);
		setCreativeTab(OpenPeripheral.tabOpenPeripheral);
		GameRegistry.registerBlock(this, "playerinventory");
		GameRegistry.registerTileEntity(TileEntityPlayerInventory.class, "playerinventory");
		setUnlocalizedName("openperipheral.playerinventory");
		setBlockBounds(0f, 0f, 0f, 1f, 0.3f, 1f);
	}

	@Override
	public void registerIcons(IconRegister registry) {
		blockIcon = registry.registerIcon("openperipheral:playerinventory");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityPlayerInventory();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return OpenPeripheral.renderId;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (!world.isRemote) {
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			if (entity instanceof EntityPlayer && tile != null && tile instanceof TileEntityPlayerInventory) {
				TileEntityPlayerInventory pi = (TileEntityPlayerInventory) tile;
				if (pi.getPlayer() == null) {
					ChunkCoordinates coordinates = ((EntityPlayer) entity).getPlayerCoordinates();
					if (coordinates.posX == x && coordinates.posY == y && coordinates.posZ == z) {
						pi.setPlayer((EntityPlayer) entity);
					}
				}
			}
		}
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.DOWN;
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

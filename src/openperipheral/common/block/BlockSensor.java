package openperipheral.common.block;

import openperipheral.OpenPeripheral;
import openperipheral.common.config.ConfigSettings;
import openperipheral.common.tileentity.TileEntityProxy;
import openperipheral.common.tileentity.TileEntitySensor;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockSensor extends BlockContainer {

	public Icon turtleIcon;
	private Icon icon;

	public BlockSensor() {
		super(ConfigSettings.sensorBlockId, Material.ground);
		setHardness(0.5F);
		setCreativeTab(OpenPeripheral.tabOpenPeripheral);
		GameRegistry.registerBlock(this, "sensor");
		GameRegistry.registerTileEntity(TileEntitySensor.class, "sensor");
		setUnlocalizedName("openperipheral.sensor");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntitySensor();
	}

	public boolean canCollideCheck(int par1, boolean par2) {
		return true;
	}

	@Override
	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
		return icon;
	}

	@Override
	public Icon getIcon(int i, int damage) {
		return icon;
	}

	@Override
	public void registerIcons(IconRegister iconRegister) {
		turtleIcon = iconRegister.registerIcon("openperipheral:turtleSensor");
		icon = iconRegister.registerIcon("openperipheral:sensor");
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return 0;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return OpenPeripheral.renderId;
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

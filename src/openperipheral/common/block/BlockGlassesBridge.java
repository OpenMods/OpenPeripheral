package openperipheral.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openperipheral.OpenPeripheral;
import openperipheral.common.tileentity.TileEntityGlassesBridge;
import openperipheral.common.util.MiscUtils;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockGlassesBridge extends BlockContainer {

	public BlockGlassesBridge() {
		super(OpenPeripheral.Config.glassesBridgeId, Material.ground);
		setHardness(0.5F);
		setCreativeTab(OpenPeripheral.tabOpenPeripheral);
		GameRegistry.registerBlock(this, "glassesbridge");
		GameRegistry.registerTileEntity(TileEntityGlassesBridge.class,
				"glassesbridge");
		setUnlocalizedName("openperipheral.glassesbridge");
	}

	@Override
	public void registerIcons(IconRegister register) {
		blockIcon = register.registerIcon("openperipheral:bridge");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityGlassesBridge();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float hitX, float hitY, float hitZ) {

	    TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (player.isSneaking() || tileEntity == null) {
			return false;
		}
		
		if (!world.isRemote && tileEntity instanceof TileEntityGlassesBridge) {
			ItemStack glassesStack = player.getHeldItem();
			if (MiscUtils.canBeGlasses(glassesStack)) {
				((TileEntityGlassesBridge)tileEntity).writeDataToGlasses(glassesStack);
			}
		}
		
		return true;
	}

}

package openperipheral.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openperipheral.OpenPeripheral;
import openperipheral.common.tileentity.TileEntityGlassesBridge;
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

}

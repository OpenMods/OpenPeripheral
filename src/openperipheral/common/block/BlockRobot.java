package openperipheral.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import openperipheral.OpenPeripheral;
import openperipheral.common.config.ConfigSettings;
import openperipheral.common.tileentity.TileEntityRobot;
import openperipheral.common.tileentity.TileEntitySensor;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRobot extends BlockContainer {

	public BlockRobot() {
		super(ConfigSettings.robotBlockId, Material.ground);
		setHardness(0.5F);
		setCreativeTab(OpenPeripheral.tabOpenPeripheral);
		GameRegistry.registerBlock(this, "robot");
		GameRegistry.registerTileEntity(TileEntityRobot.class, "robot");
		setUnlocalizedName("openperipheral.robot");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityRobot();
	}

}

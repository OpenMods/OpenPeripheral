package openperipheral.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.OpenPeripheral;
import openperipheral.common.config.ConfigSettings;
import openperipheral.common.tileentity.TileEntityTicketMachine;
import openperipheral.common.util.BlockUtils;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockTicketMachine extends BlockContainer {

	public BlockTicketMachine() {
		super(ConfigSettings.ticketMachineId, Material.ground);
		setHardness(0.5F);
		setCreativeTab(OpenPeripheral.tabOpenPeripheral);
		GameRegistry.registerBlock(this, "ticketmachine");
		GameRegistry.registerTileEntity(TileEntityTicketMachine.class, "ticketmachine");
		setUnlocalizedName("openperipheral.ticketmachine");
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
	}

}

package openperipheral.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import openperipheral.OpenPeripheral;
import openperipheral.common.block.BlockGlassesBridge;
import openperipheral.common.block.BlockPlayerInventory;
import openperipheral.common.block.BlockProxy;
import openperipheral.common.block.BlockTicketMachine;
import openperipheral.common.container.ContainerGeneric;
import openperipheral.common.core.Mods;
import openperipheral.common.item.ItemGlasses;
import openperipheral.common.tileentity.TileEntityTicketMachine;
import openperipheral.common.util.LanguageUtils;
import openperipheral.common.util.RecipeUtils;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;

public class CommonProxy implements IGuiHandler {

	public void init() {

		OpenPeripheral.Items.glasses = new ItemGlasses();

		OpenPeripheral.Blocks.glassesBridge = new BlockGlassesBridge();
		OpenPeripheral.Blocks.proxy = new BlockProxy();
		OpenPeripheral.Blocks.playerInventory = new BlockPlayerInventory();
		if (Loader.isModLoaded(Mods.RAILCRAFT)) {
			OpenPeripheral.Blocks.ticketMachine = new BlockTicketMachine();
		}
		setupLanguages();

		RecipeUtils.addGlassesRecipe();
		RecipeUtils.addBridgeRecipe();
		RecipeUtils.addBookRecipe();

		MinecraftForge.EVENT_BUS.register(new ChatCommandInterceptor());

		NetworkRegistry.instance().registerGuiHandler(OpenPeripheral.instance, this);
	
	}

	public void registerRenderInformation() {
	}

	private void setupLanguages() {
		LanguageUtils.setupLanguages();
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (ID == OpenPeripheral.Gui.ticketMachine.ordinal()) {
			return new ContainerGeneric(player.inventory, tile, TileEntityTicketMachine.SLOTS);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
}

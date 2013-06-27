package openperipheral.common;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import openperipheral.OpenPeripheral;
import openperipheral.common.block.BlockGlassesBridge;
import openperipheral.common.block.BlockPlayerInventory;
import openperipheral.common.block.BlockProxy;
import openperipheral.common.block.BlockSensor;
import openperipheral.common.block.BlockTicketMachine;
import openperipheral.common.container.ContainerComputer;
import openperipheral.common.container.ContainerGeneric;
import openperipheral.common.core.Mods;
import openperipheral.common.item.ItemGlasses;
import openperipheral.common.item.ItemRemote;
import openperipheral.common.tileentity.TileEntityTicketMachine;
import openperipheral.common.util.LanguageUtils;
import openperipheral.common.util.RecipeUtils;
import openperipheral.common.util.ReflectionHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkModHandler;
import cpw.mods.fml.common.network.NetworkRegistry;

public class CommonProxy implements IGuiHandler {

	public void init() {

		OpenPeripheral.Items.glasses = new ItemGlasses();
		OpenPeripheral.Items.remote = new ItemRemote();

		OpenPeripheral.Blocks.glassesBridge = new BlockGlassesBridge();
		OpenPeripheral.Blocks.proxy = new BlockProxy();
		OpenPeripheral.Blocks.playerInventory = new BlockPlayerInventory();
		OpenPeripheral.Blocks.sensor = new BlockSensor();
		
		if (Loader.isModLoaded(Mods.RAILCRAFT)) {
			OpenPeripheral.Blocks.ticketMachine = new BlockTicketMachine();
			RecipeUtils.addTicketMachineRecipe();
		}
		
		setupLanguages();

		RecipeUtils.addGlassesRecipe();
		RecipeUtils.addBridgeRecipe();
		RecipeUtils.addBookRecipe();
		RecipeUtils.addProxyRecipe();
		RecipeUtils.addPIMRecipe();

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
		}else if (ID == OpenPeripheral.Gui.remote.ordinal()) {
			return new ContainerComputer();
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
}

package openperipheral.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import openperipheral.OpenPeripheral;
import openperipheral.api.IRobot;
import openperipheral.common.block.BlockGlassesBridge;
import openperipheral.common.block.BlockPlayerInventory;
import openperipheral.common.block.BlockProxy;
import openperipheral.common.block.BlockRobot;
import openperipheral.common.block.BlockSensor;
import openperipheral.common.block.BlockTicketMachine;
import openperipheral.common.container.ContainerComputer;
import openperipheral.common.container.ContainerGeneric;
import openperipheral.common.container.ContainerRobot;
import openperipheral.common.core.Mods;
import openperipheral.common.entity.EntityLazer;
import openperipheral.common.entity.EntityRobot;
import openperipheral.common.item.ItemGeneric;
import openperipheral.common.item.ItemGlasses;
import openperipheral.common.item.ItemRemote;
import openperipheral.common.item.ItemRobot;
import openperipheral.common.tileentity.TileEntityRobot;
import openperipheral.common.tileentity.TileEntityTicketMachine;
import openperipheral.common.util.LanguageUtils;
import openperipheral.common.util.RecipeUtils;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;

public class CommonProxy implements IGuiHandler {

	public void init() {

		OpenPeripheral.Items.glasses = new ItemGlasses();
		OpenPeripheral.Items.remote = new ItemRemote();
		OpenPeripheral.Items.robot = new ItemRobot();
		OpenPeripheral.Items.generic = new ItemGeneric();
		OpenPeripheral.Items.generic.initRecipes();

		OpenPeripheral.Blocks.glassesBridge = new BlockGlassesBridge();
		OpenPeripheral.Blocks.proxy = new BlockProxy();
		OpenPeripheral.Blocks.playerInventory = new BlockPlayerInventory();
		OpenPeripheral.Blocks.sensor = new BlockSensor();
		OpenPeripheral.Blocks.robot = new BlockRobot();

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
		RecipeUtils.addRemoteRecipe();

		MinecraftForge.EVENT_BUS.register(new ChatCommandInterceptor());

		EntityRegistry.registerModEntity(EntityRobot.class, "Robot", 600, OpenPeripheral.instance, 64, 1, true);
		EntityRegistry.registerModEntity(EntityLazer.class, "Lazer", 601, OpenPeripheral.instance, 64, 1, true);

		NetworkRegistry.instance().registerGuiHandler(OpenPeripheral.instance, this);

	}

	public void registerRenderInformation() {
	}

	private void setupLanguages() {
		LanguageUtils.setupLanguages();
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == OpenPeripheral.Gui.robotEntity.ordinal()) {
			return new ContainerRobot(player.inventory, (IRobot)player.worldObj.getEntityByID(x));
		}
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (ID == OpenPeripheral.Gui.ticketMachine.ordinal()) {
			return new ContainerGeneric(player.inventory, (IInventory)tile, TileEntityTicketMachine.SLOTS);
		} else if (ID == OpenPeripheral.Gui.remote.ordinal()) {
			return new ContainerComputer();
		} else if (ID == OpenPeripheral.Gui.robot.ordinal()) {
			return new ContainerGeneric(player.inventory, (IInventory)tile, TileEntityRobot.SLOTS);
		} 
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
}

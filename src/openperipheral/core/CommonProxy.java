package openperipheral.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import openperipheral.OpenPeripheral;
import openperipheral.api.IRobot;
import openperipheral.core.block.BlockPlayerInventory;
import openperipheral.core.block.BlockProxy;
import openperipheral.core.block.BlockTicketMachine;
import openperipheral.core.block.TileEntityTicketMachine;
import openperipheral.core.container.ContainerComputer;
import openperipheral.core.container.ContainerGeneric;
import openperipheral.core.item.ItemGeneric;
import openperipheral.core.item.ItemGlasses;
import openperipheral.core.item.ItemRemote;
import openperipheral.core.item.ItemRobot;
import openperipheral.core.util.LanguageUtils;
import openperipheral.core.util.RecipeUtils;
import openperipheral.glasses.block.BlockGlassesBridge;
import openperipheral.robots.ContainerRobot;
import openperipheral.robots.block.BlockRobot;
import openperipheral.robots.block.TileEntityRobot;
import openperipheral.robots.entity.EntityLazer;
import openperipheral.robots.entity.EntityRobotWarrior;
import openperipheral.sensor.block.BlockSensor;
import openperipheral.turtle.TurtleUpgradeNarcissistic;
import openperipheral.turtle.TurtleUpgradeSensor;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import dan200.turtle.api.TurtleAPI;

public class CommonProxy implements IGuiHandler {

	public void init() {

		OpenPeripheral.Items.glasses = new ItemGlasses();
		OpenPeripheral.Items.remote = new ItemRemote();
		OpenPeripheral.Items.generic = new ItemGeneric();
		if (ConfigSettings.robotsEnabled) {
			OpenPeripheral.Items.robot = new ItemRobot();
		}
		OpenPeripheral.Items.generic.initRecipes();

		OpenPeripheral.Blocks.glassesBridge = new BlockGlassesBridge();
		OpenPeripheral.Blocks.proxy = new BlockProxy();
		OpenPeripheral.Blocks.playerInventory = new BlockPlayerInventory();
		OpenPeripheral.Blocks.sensor = new BlockSensor();
		if (ConfigSettings.robotsEnabled) {
			OpenPeripheral.Blocks.robot = new BlockRobot();
		}

		TurtleAPI.registerUpgrade(new TurtleUpgradeSensor());
		TurtleAPI.registerUpgrade(new TurtleUpgradeNarcissistic());

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
		RecipeUtils.addSensorRecipe();
		RecipeUtils.addRobotRecipe();

		MinecraftForge.EVENT_BUS.register(new ChatCommandInterceptor());

		if (ConfigSettings.robotsEnabled) {
			EntityRegistry.registerModEntity(EntityRobotWarrior.class, "RobotWarrior", 600, OpenPeripheral.instance, 64, 1, true);
			EntityRegistry.registerModEntity(EntityLazer.class, "Lazer", 701, OpenPeripheral.instance, 64, 1, true);
		}

		NetworkRegistry.instance().registerGuiHandler(OpenPeripheral.instance, this);

	}

	public void registerRenderInformation() {}

	private void setupLanguages() {
		LanguageUtils.setupLanguages();
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == OpenPeripheral.Gui.robotEntity.ordinal()) { return new ContainerRobot(player.inventory, (IRobot)player.worldObj.getEntityByID(x)); }
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (ID == OpenPeripheral.Gui.ticketMachine.ordinal()) {
			return new ContainerGeneric(player.inventory, (IInventory)tile, TileEntityTicketMachine.SLOTS);
		} else if (ID == OpenPeripheral.Gui.remote.ordinal()) {
			return new ContainerComputer();
		} else if (ID == OpenPeripheral.Gui.robot.ordinal()) { return new ContainerGeneric(player.inventory, (IInventory)tile, TileEntityRobot.SLOTS); }
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
}

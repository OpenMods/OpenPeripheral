package openperipheral.core.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import openperipheral.OpenPeripheral;
import openperipheral.api.IRobot;
import openperipheral.core.CommonProxy;
import openperipheral.core.ConfigSettings;
import openperipheral.core.Mods;
import openperipheral.core.block.TileEntityPlayerInventory;
import openperipheral.core.block.TileEntitySensor;
import openperipheral.core.block.TileEntityTicketMachine;
import openperipheral.core.container.ContainerComputer;
import openperipheral.core.container.ContainerGeneric;
import openperipheral.core.util.GuiUtils;
import openperipheral.glasses.client.TerminalManager;
import openperipheral.robots.ContainerRobot;
import openperipheral.robots.block.TileEntityRobot;
import openperipheral.robots.client.GuiRobot;
import openperipheral.robots.client.GuiRobotEntity;
import openperipheral.robots.client.ModelRobotWarrior;
import openperipheral.robots.client.RenderLazer;
import openperipheral.robots.client.RenderRobotWarrior;
import openperipheral.robots.client.TileEntityRobotRenderer;
import openperipheral.robots.entity.EntityLazer;
import openperipheral.robots.entity.EntityRobotWarrior;
import openperipheral.sensor.client.TileEntitySensorRenderer;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.NetworkRegistry;

public class ClientProxy extends CommonProxy {

	public ClientProxy() {
		MinecraftForge.EVENT_BUS.register(new SoundLoader());
	}

	public static TerminalManager terminalManager = new TerminalManager();
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if ((world instanceof WorldClient)) {
			if (ID == OpenPeripheral.Gui.robotEntity.ordinal()) {
				return new GuiRobotEntity(new ContainerRobot(player.inventory, (IRobot)world.getEntityByID(x)));
			}
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			if (ID == OpenPeripheral.Gui.ticketMachine.ordinal()) {
				return new GuiTicketMachine(new ContainerGeneric(player.inventory, (IInventory)tile, TileEntityTicketMachine.SLOTS), (TileEntityTicketMachine) tile);
			}else if (ID == OpenPeripheral.Gui.remote.ordinal()) {
				GuiContainer screen = GuiUtils.getGuiContainerForMod(Mods.COMPUTERCRAFT, player, world, x, y, z);
				if (screen != null) {
	        		screen.inventorySlots = new ContainerComputer();
	        	}
				return screen;
			}else if (ID == OpenPeripheral.Gui.robot.ordinal()) {
				return new GuiRobot(new ContainerGeneric(player.inventory, (IInventory)tile, TileEntityRobot.SLOTS), (TileEntityRobot) tile);
			}
		}
		return null;
	}

	@Override
	public void registerRenderInformation() {

		OpenPeripheral.renderId = RenderingRegistry.getNextAvailableRenderId();
		MinecraftForge.EVENT_BUS.register(terminalManager);
		NetworkRegistry.instance().registerConnectionHandler(terminalManager);

		RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlayerInventory.class, new TileEntityPlayerInventoryRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySensor.class, new TileEntitySensorRenderer());
		

		if (ConfigSettings.robotsEnabled) {
			
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRobot.class, new TileEntityRobotRenderer());
	
			RenderingRegistry.registerEntityRenderingHandler(EntityRobotWarrior.class, new RenderRobotWarrior(new ModelRobotWarrior(), 0.7F));
			RenderingRegistry.registerEntityRenderingHandler(EntityLazer.class, new RenderLazer());
		}
	}
}

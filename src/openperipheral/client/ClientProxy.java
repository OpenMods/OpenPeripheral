package openperipheral.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import openperipheral.OpenPeripheral;
import openperipheral.api.IRobot;
import openperipheral.client.gui.GuiRobot;
import openperipheral.client.gui.GuiRobotEntity;
import openperipheral.client.gui.GuiTicketMachine;
import openperipheral.client.model.ModelRobot;
import openperipheral.client.renderer.RenderLazer;
import openperipheral.client.renderer.RenderRobot;
import openperipheral.client.renderer.TileEntityPlayerInventoryRenderer;
import openperipheral.client.renderer.TileEntityRobotRenderer;
import openperipheral.client.renderer.TileEntitySensorRenderer;
import openperipheral.common.CommonProxy;
import openperipheral.common.container.ContainerComputer;
import openperipheral.common.container.ContainerGeneric;
import openperipheral.common.container.ContainerRobot;
import openperipheral.common.core.Mods;
import openperipheral.common.entity.EntityLazer;
import openperipheral.common.entity.EntityRobot;
import openperipheral.common.tileentity.TileEntityPlayerInventory;
import openperipheral.common.tileentity.TileEntityRobot;
import openperipheral.common.tileentity.TileEntitySensor;
import openperipheral.common.tileentity.TileEntityTicketMachine;
import openperipheral.common.util.GuiUtils;
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
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRobot.class, new TileEntityRobotRenderer());

		RenderingRegistry.registerEntityRenderingHandler(EntityRobot.class, new RenderRobot(new ModelRobot(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityLazer.class, new RenderLazer());
	}
}

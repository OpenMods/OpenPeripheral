package openperipheral.client;

import java.util.Map;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import openperipheral.OpenPeripheral;
import openperipheral.client.gui.GuiTicketMachine;
import openperipheral.client.renderer.TileEntityPlayerInventoryRenderer;
import openperipheral.client.renderer.TileEntitySensorRenderer;
import openperipheral.common.CommonProxy;
import openperipheral.common.container.ContainerComputer;
import openperipheral.common.container.ContainerGeneric;
import openperipheral.common.core.Mods;
import openperipheral.common.core.TickHandler;
import openperipheral.common.tileentity.TileEntityPlayerInventory;
import openperipheral.common.tileentity.TileEntitySensor;
import openperipheral.common.tileentity.TileEntityTicketMachine;
import openperipheral.common.util.GuiUtils;
import openperipheral.common.util.ReflectionHelper;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkModHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

	public ClientProxy() {
		MinecraftForge.EVENT_BUS.register(new SoundLoader());
	}

	public static TerminalManager terminalManager = new TerminalManager();
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if ((world instanceof WorldClient)) {
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			if (ID == OpenPeripheral.Gui.ticketMachine.ordinal()) {
				return new GuiTicketMachine(new ContainerGeneric(player.inventory, tile, TileEntityTicketMachine.SLOTS), (TileEntityTicketMachine) tile);
			}else if (ID == OpenPeripheral.Gui.remote.ordinal()) {
				GuiContainer screen = GuiUtils.getGuiContainerForMod(Mods.COMPUTERCRAFT, player, world, x, y, z);
				if (screen != null) {
	        		screen.inventorySlots = new ContainerComputer();
	        	}
	        	return screen;
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

	}
}

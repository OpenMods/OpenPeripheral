package openperipheral.client;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import openperipheral.OpenPeripheral;
import openperipheral.client.gui.GuiTicketMachine;
import openperipheral.client.renderer.TileEntityPlayerInventoryRenderer;
import openperipheral.common.CommonProxy;
import openperipheral.common.container.ContainerGeneric;
import openperipheral.common.tileentity.TileEntityPlayerInventory;
import openperipheral.common.tileentity.TileEntityTicketMachine;
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
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			if (ID == OpenPeripheral.Gui.ticketMachine.ordinal()) {
				return new GuiTicketMachine(new ContainerGeneric(player.inventory, tile, TileEntityTicketMachine.SLOTS), (TileEntityTicketMachine) tile);
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

	}
}

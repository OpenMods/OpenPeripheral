package openperipheral.client;

import net.minecraftforge.common.MinecraftForge;
import openperipheral.OpenPeripheral;
import openperipheral.common.CommonProxy;
import openperipheral.common.tileentity.TileEntityPlayerInventory;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.NetworkRegistry;

public class ClientProxy extends CommonProxy {

	public static TerminalManager terminalManager = new TerminalManager();

	@Override
	public void registerRenderInformation() {
		
		OpenPeripheral.renderId = RenderingRegistry.getNextAvailableRenderId();
		MinecraftForge.EVENT_BUS.register(terminalManager);
		NetworkRegistry.instance().registerConnectionHandler(terminalManager);

		RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlayerInventory.class, new TileEntityPlayerInventoryRenderer());

	}
}

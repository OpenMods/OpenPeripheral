package openperipheral;

import java.io.File;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import openmods.config.properties.ConfigProcessing;
import openperipheral.adapter.PeripheralHandlers;
import openperipheral.adapter.TileEntityBlacklist;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.*;
import dan200.computercraft.api.ComputerCraftAPI;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDENCIES)
public class OpenPeripheralCore {

	static {
		ApiProvider.installApi();
	}

	public static final String PROVIDED_API_VERSION = "2.1";

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		final File configFile = evt.getSuggestedConfigurationFile();
		Configuration config = new Configuration(configFile);
		ConfigProcessing.processAnnotations(configFile, ModInfo.ID, config, Config.class);
		if (config.hasChanged()) config.save();

		MinecraftForge.EVENT_BUS.register(TileEntityBlacklist.INSTANCE);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent evt) {
		ClientCommandHandler.instance.registerCommand(new CommandDump());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		ComputerCraftAPI.registerPeripheralProvider(new PeripheralHandlers());
	}

	@EventHandler
	public void processMessage(FMLInterModComms.IMCEvent event) {
		for (FMLInterModComms.IMCMessage m : event.getMessages()) {
			if (m.isStringMessage() && "ignoreTileEntity".equalsIgnoreCase(m.key)) {
				TileEntityBlacklist.INSTANCE.addClass(m.getStringValue());
			}
		}
	}
}
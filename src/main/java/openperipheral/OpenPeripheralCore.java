package openperipheral;

import java.io.File;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.config.Configuration;
import openmods.config.properties.ConfigProcessing;
import openperipheral.adapter.PeripheralHandlers;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dan200.computercraft.api.ComputerCraftAPI;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDENCIES)
public class OpenPeripheralCore {

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		final File configFile = evt.getSuggestedConfigurationFile();
		Configuration config = new Configuration(configFile);
		ConfigProcessing.processAnnotations(configFile, ModInfo.ID, config, Config.class);
		if (config.hasChanged()) config.save();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent evt) {
		ClientCommandHandler.instance.registerCommand(new CommandDump());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		ComputerCraftAPI.registerPeripheralProvider(new PeripheralHandlers());
	}
}
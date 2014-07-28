package openperipheral;

import java.io.File;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.Configuration;
import openmods.config.ConfigProcessing;
import openperipheral.adapter.PeripheralHandlers;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
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
		TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);

		// not side only, so no proxy needed
		ClientCommandHandler.instance.registerCommand(new CommandDump());

	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		ComputerCraftAPI.registerPeripheralProvider(new PeripheralHandlers());
	}
}
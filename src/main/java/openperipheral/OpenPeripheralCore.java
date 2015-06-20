package openperipheral;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import openmods.Log;
import openmods.Mods;
import openmods.config.properties.ConfigProcessing;
import openperipheral.adapter.PeripheralTypeProvider;
import openperipheral.adapter.TileEntityBlacklist;
import openperipheral.api.Constants;
import openperipheral.api.peripheral.IOpenPeripheral;
import openperipheral.interfaces.cc.ModuleComputerCraft;
import openperipheral.interfaces.oc.ModuleOpenComputers;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.*;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDENCIES, acceptableRemoteVersions = "*")
public class OpenPeripheralCore {

	static {
		ApiProvider.installApi();
	}

	public static final String PROVIDED_API_VERSION = "3.3";

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		PeripheralTypeProvider.INSTANCE.initialize(evt.getModConfigurationDirectory());

		final File configFile = evt.getSuggestedConfigurationFile();
		Configuration config = new Configuration(configFile);
		ConfigProcessing.processAnnotations(ModInfo.ID, config, Config.class);
		if (config.hasChanged()) config.save();

		MinecraftForge.EVENT_BUS.register(TileEntityBlacklist.INSTANCE);

		FMLInterModComms.sendMessage(Mods.OPENCOMPUTERS, "blacklistPeripheral", IOpenPeripheral.class.getName());

		if (Loader.isModLoaded(Mods.OPENCOMPUTERS)) ModuleOpenComputers.init();
		if (Loader.isModLoaded(Mods.COMPUTERCRAFT)) ModuleComputerCraft.init();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent evt) {
		if (Loader.isModLoaded(Mods.OPENCOMPUTERS)) ModuleOpenComputers.registerProvider();
	}

	// this method should be called as late as possible, to make sure we are last on provider list
	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent evt) {
		if (Loader.isModLoaded(Mods.COMPUTERCRAFT)) ModuleComputerCraft.registerProvider();
	}

	@EventHandler
	public void processMessage(FMLInterModComms.IMCEvent event) {
		for (FMLInterModComms.IMCMessage m : event.getMessages()) {
			if (m.isStringMessage()) {
				if (Constants.IMC_IGNORE.equalsIgnoreCase(m.key)) {
					TileEntityBlacklist.INSTANCE.addToBlacklist(m.getStringValue());
				} else if (Constants.IMC_NAME_CLASS.equalsIgnoreCase(m.key)) {
					String value = m.getStringValue();
					String[] fields = value.split("\\s+");
					if (fields.length != 2) {
						Log.warn("Invalid IMC from %s: can't decode type '%s'", m.getSender(), value);
					} else {
						PeripheralTypeProvider.INSTANCE.setType(fields[0], fields[1]);
					}
				}
			}
		}
	}

	@EventHandler
	public void severStart(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new CommandDump("op_dump", evt.getServer().isDedicatedServer()));
	}

}
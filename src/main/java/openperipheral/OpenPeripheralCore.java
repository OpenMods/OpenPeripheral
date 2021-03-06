package openperipheral;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import java.io.File;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import openmods.Log;
import openmods.Mods;
import openmods.config.properties.ConfigProcessing;
import openperipheral.adapter.FeatureGroupManager;
import openperipheral.adapter.PeripheralTypeProvider;
import openperipheral.adapter.TileEntityBlacklist;
import openperipheral.adapter.types.classifier.MinecraftTypeClassifier;
import openperipheral.adapter.types.classifier.TypeClassifier;
import openperipheral.api.Constants;
import openperipheral.api.peripheral.IOpenPeripheral;
import openperipheral.interfaces.cc.ComputerCraftChecker;
import openperipheral.interfaces.cc.ModuleComputerCraft;
import openperipheral.interfaces.oc.ModuleOpenComputers;
import openperipheral.interfaces.oc.OpenComputersChecker;

@Mod(modid = ModInfo.ID,
		name = ModInfo.NAME,
		version = ModInfo.VERSION,
		dependencies = ModInfo.DEPENDENCIES,
		acceptableRemoteVersions = "*",
		guiFactory = "openperipheral.ConfigGuiFactory")
public class OpenPeripheralCore {

	private final ApiSetup apiSetup = new ApiSetup();

	@Instance(ModInfo.ID)
	public static OpenPeripheralCore instance;

	private Configuration config;

	Configuration config() {
		return config;
	}

	@Mod.EventHandler
	public void construct(FMLConstructionEvent evt) {
		ArchitectureChecker.INSTANCE.register(Constants.ARCH_COMPUTER_CRAFT, new ComputerCraftChecker());
		ArchitectureChecker.INSTANCE.register(Constants.ARCH_OPEN_COMPUTERS, new OpenComputersChecker());

		apiSetup.setupApis();
		apiSetup.installProviderAccess();

		if (ArchitectureChecker.INSTANCE.isEnabled(Constants.ARCH_OPEN_COMPUTERS)) ModuleOpenComputers.init();
		if (ArchitectureChecker.INSTANCE.isEnabled(Constants.ARCH_COMPUTER_CRAFT)) ModuleComputerCraft.init();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		apiSetup.installHolderAccess(evt.getAsmData());
		PeripheralTypeProvider.INSTANCE.initialize(evt.getModConfigurationDirectory());

		final File configFile = evt.getSuggestedConfigurationFile();
		config = new Configuration(configFile);
		ConfigProcessing.processAnnotations(ModInfo.ID, config, Config.class);
		if (config.hasChanged()) config.save();

		FeatureGroupManager.INSTANCE.loadBlacklist(Config.featureGroupsBlacklist);
		FMLCommonHandler.instance().bus().register(new ConfigGuiFactory.ConfigChangeListener(config));

		MinecraftForge.EVENT_BUS.register(TileEntityBlacklist.INSTANCE);

		FMLInterModComms.sendMessage(Mods.OPENCOMPUTERS, "blacklistPeripheral", IOpenPeripheral.class.getName());

		TypeClassifier.INSTANCE.registerClassifier(new MinecraftTypeClassifier());

		FeatureGroupManager.INSTANCE.loadFeatureGroupsFromAnnotations(evt.getAsmData());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent evt) {
		if (ArchitectureChecker.INSTANCE.isEnabled(Constants.ARCH_OPEN_COMPUTERS)) ModuleOpenComputers.registerProvider();
	}

	// this method should be called as late as possible, to make sure we are last on provider list
	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent evt) {
		if (ArchitectureChecker.INSTANCE.isEnabled(Constants.ARCH_COMPUTER_CRAFT)) ModuleComputerCraft.registerProvider();
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
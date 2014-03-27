package openperipheral;

import java.io.File;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.Configuration;
import openmods.config.ConfigProcessing;
import openperipheral.adapter.PeripheralHandlers;
import openperipheral.integration.appeng.ModuleAppEng;
import openperipheral.integration.buildcraft.ModuleBuildCraft;
import openperipheral.integration.computercraft.ModuleComputerCraft;
import openperipheral.integration.enderstorage.ModuleEnderStorage;
import openperipheral.integration.forestry.ModuleForestry;
import openperipheral.integration.ic2.ModuleIC2;
import openperipheral.integration.minefactoryreloaded.ModuleMinefactoryReloaded;
import openperipheral.integration.multiparts.ModuleMultiparts;
import openperipheral.integration.mystcraft.ModuleMystcraft;
import openperipheral.integration.projectred.ModuleProjectRed;
import openperipheral.integration.railcraft.ModuleRailcraft;
import openperipheral.integration.sgcraft.ModuleSgCraft;
import openperipheral.integration.thaumcraft.ModuleThaumcraft;
import openperipheral.integration.thermalexpansion.ModuleThermalExpansion;
import openperipheral.integration.tmechworks.ModuleTMechworks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import dan200.computercraft.api.ComputerCraftAPI;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDENCIES)
@NetworkMod(serverSideRequired = true, clientSideRequired = false, channels = { ModInfo.ID })
public class OpenPeripheralCore {

	@Instance(value = ModInfo.ID)
	public static OpenPeripheralCore instance;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		final File configFile = evt.getSuggestedConfigurationFile();
		Configuration config = new Configuration(configFile);
		ConfigProcessing.processAnnotations(configFile, ModInfo.ID, config, Config.class);
		if (config.hasChanged()) config.save();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent evt) {
		IntegrationModuleRegistry.registerModule(new ModuleAppEng());
		IntegrationModuleRegistry.registerModule(new ModuleIC2());
		IntegrationModuleRegistry.registerModule(new ModuleEnderStorage());
		IntegrationModuleRegistry.registerModule(new ModuleBuildCraft());
		IntegrationModuleRegistry.registerModule(new ModuleForestry());
		IntegrationModuleRegistry.registerModule(new ModuleMystcraft());
		IntegrationModuleRegistry.registerModule(new ModuleProjectRed());
		IntegrationModuleRegistry.registerModule(new ModuleRailcraft());
		IntegrationModuleRegistry.registerModule(new ModuleThaumcraft());
		IntegrationModuleRegistry.registerModule(new ModuleThermalExpansion());
		IntegrationModuleRegistry.registerModule(new ModuleSgCraft());
		IntegrationModuleRegistry.registerModule(new ModuleTMechworks());
		IntegrationModuleRegistry.registerModule(new ModuleMultiparts());
		IntegrationModuleRegistry.registerModule(new ModuleComputerCraft());
		IntegrationModuleRegistry.registerModule(new ModuleMinefactoryReloaded());

		IntegrationModuleRegistry.selectLoadedModules();
		IntegrationModuleRegistry.initAllModules();

		TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);

		// not side only, so no proxy needed
		ClientCommandHandler.instance.registerCommand(new CommandDump());

	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		ComputerCraftAPI.registerPeripheralProvider(new PeripheralHandlers());
	}
}
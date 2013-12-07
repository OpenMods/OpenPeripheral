package openperipheral;

import java.util.Map;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.Configuration;
import openperipheral.adapter.AdapterObject;
import openperipheral.adapter.vanilla.AdapterFluidHandler;
import openperipheral.integration.*;
import openperipheral.peripheral.PeripheralHandler;
import openperipheral.util.ReflectionHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import dan200.computer.api.ComputerCraftAPI;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDENCIES)
@NetworkMod(serverSideRequired = true, clientSideRequired = false, channels = { ModInfo.ID })
public class OpenPeripheralCore {

	@Instance(value = ModInfo.ID)
	public static OpenPeripheralCore instance;

	public static final PeripheralHandler peripheralHandler = new PeripheralHandler();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		Configuration configFile = new Configuration(evt.getSuggestedConfigurationFile());
		Config.readConfig(configFile);
		if (configFile.hasChanged()) {
			configFile.save();
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent evt) {
		AdapterManager.addPeripheralAdapter(new AdapterObject());
		AdapterManager.addPeripheralAdapter(new AdapterFluidHandler());

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

		IntegrationModuleRegistry.selectLoadedModules();
		IntegrationModuleRegistry.initAllModules();

		TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);

	}

	@SuppressWarnings("unchecked")
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		Map<Class<? extends TileEntity>, String> classToNameMap = (Map<Class<? extends TileEntity>, String>)ReflectionHelper.getProperty("net.minecraft.tileentity.TileEntity", null, "classToNameMap", "field_70323_b");
		Set<Class<? extends TileEntity>> teClasses = classToNameMap.keySet();
		for (Class<?> klazz : AdapterManager.getRegisteredClasses()) {
			if (!klazz.equals(Object.class)) {
				for (Class<? extends TileEntity> teClass : teClasses) {
					if (klazz.isAssignableFrom(teClass)) {
						ComputerCraftAPI.registerExternalPeripheral(teClass, peripheralHandler);
					}
				}
			}
		}
	}
}
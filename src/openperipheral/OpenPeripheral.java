package openperipheral;

import java.util.Map;
import java.util.Set;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.Configuration;
import openmods.Log;
import openmods.Mods;
import openmods.api.IOpenMod;
import openperipheral.adapter.AdapterObject;
import openperipheral.adapter.vanilla.AdapterFluidHandler;
import openperipheral.converter.*;
import openperipheral.integration.*;
import openperipheral.peripheral.PeripheralHandler;
import openperipheral.util.BasicMount;
import openperipheral.util.ReflectionHelper;
import cpw.mods.fml.common.Loader;
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
public class OpenPeripheral implements IOpenMod {

	@Instance(value = ModInfo.ID)
	public static OpenPeripheral instance;

	public static BasicMount mount = new BasicMount();

	public static PeripheralHandler peripheralHandler = new PeripheralHandler();

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
		TypeConversionRegistry.registerTypeConverter(new ConverterArray());
		TypeConversionRegistry.registerTypeConverter(new ConverterList());
		TypeConversionRegistry.registerTypeConverter(new ConverterDouble());
		TypeConversionRegistry.registerTypeConverter(new ConverterItemStack());
		TypeConversionRegistry.registerTypeConverter(new ConverterFluidTankInfo());
		TypeConversionRegistry.registerTypeConverter(new ConverterForgeDirection());
		TypeConversionRegistry.registerTypeConverter(new ConverterFluidTankInfo());

		AdapterManager.addPeripheralAdapter(new AdapterObject());
		AdapterManager.addPeripheralAdapter(new AdapterFluidHandler());

		ModuleVanilla.init();

		if (Loader.isModLoaded(Mods.APPLIEDENERGISTICS)) {
			ModuleAppEng.init();
		}

		if (Loader.isModLoaded(Mods.IC2)) {
			ModuleIC2.init();
		}

		if (Loader.isModLoaded(Mods.ENDERSTORAGE)) {
			ModuleEnderStorage.init();
		}

		if (Loader.isModLoaded(Mods.BUILDCRAFT)) {
			ModuleBuildCraft.init();
		}

		if (Loader.isModLoaded(Mods.FORESTRY)) {
			ModuleForestry.init();
		}

		if (Loader.isModLoaded(Mods.MYSTCRAFT)) {
			ModuleMystcraft.init();
		}

		if (Loader.isModLoaded(Mods.PROJECTRED_TRANSMISSION)) {
			ModuleProjectRed.init();
		}
		if (Loader.isModLoaded(Mods.RAILCRAFT)) {
			ModuleRailcraft.init();
		}

		if (Loader.isModLoaded(Mods.THAUMCRAFT)) {
			ModuleThaumcraft.init();
		}

		if (Loader.isModLoaded(Mods.THERMALEXPANSION)) {
			ModuleThermalExpansion.init();
		}

		if (Loader.isModLoaded(Mods.SGCRAFT)) {
			ModuleSgCraft.init();
		}

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

	@Override
	public Log getLog() {
		return null;
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return null;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public int getRenderId() {
		return 0;
	}

}
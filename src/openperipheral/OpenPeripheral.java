package openperipheral;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import openblocks.IOpenBlocksProxy;
import openmods.Log;
import openmods.Mods;
import openmods.api.IOpenMod;
import openperipheral.core.AdapterManager;
import openperipheral.core.BasicMount;
import openperipheral.core.ConfigSettings;
import openperipheral.core.PeripheralHandler;
import openperipheral.core.TypeConversionRegistry;
import openperipheral.core.adapter.AdapterObject;
import openperipheral.core.adapter.vanilla.AdapterFluidHandler;
import openperipheral.core.converter.ConverterArray;
import openperipheral.core.converter.ConverterDouble;
import openperipheral.core.converter.ConverterFluidTankInfo;
import openperipheral.core.converter.ConverterForgeDirection;
import openperipheral.core.converter.ConverterItemStack;
import openperipheral.core.converter.ConverterList;
import openperipheral.core.integration.ModuleAppEng;
import openperipheral.core.integration.ModuleBuildCraft;
import openperipheral.core.integration.ModuleEnderStorage;
import openperipheral.core.integration.ModuleForestry;
import openperipheral.core.integration.ModuleIC2;
import openperipheral.core.integration.ModuleMystcraft;
import openperipheral.core.integration.ModuleProjectRed;
import openperipheral.core.integration.ModuleRailcraft;
import openperipheral.core.integration.ModuleSgCraft;
import openperipheral.core.integration.ModuleThaumcraft;
import openperipheral.core.integration.ModuleThermalExpansion;
import openperipheral.core.integration.ModuleVanilla;
import openperipheral.core.util.MountingUtils;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import dan200.computer.api.ComputerCraftAPI;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDENCIES)
@NetworkMod(serverSideRequired = true, clientSideRequired = false, channels = { ModInfo.ID })
public class OpenPeripheral implements IOpenMod {

	@Instance(value = ModInfo.ID)
	public static OpenPeripheral instance;

	@SidedProxy(clientSide = ModInfo.PROXY_CLIENT, serverSide = ModInfo.PROXY_SERVER)
	public static IOpenBlocksProxy proxy;

	public static BasicMount mount = new BasicMount();

	public static PeripheralHandler peripheralHandler = new PeripheralHandler();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		ConfigSettings.loadAndSaveConfig(evt.getSuggestedConfigurationFile());
		MountingUtils.refreshLatestFiles();
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

		ComputerCraftAPI.registerExternalPeripheral(TileEntity.class, peripheralHandler);
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
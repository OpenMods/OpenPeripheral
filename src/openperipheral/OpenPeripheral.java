package openperipheral;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import openmods.Log;
import openmods.Mods;
import openmods.api.IOpenMod;
import openperipheral.adapter.AdapterObject;
import openperipheral.adapter.vanilla.AdapterFluidHandler;
import openperipheral.converter.ConverterArray;
import openperipheral.converter.ConverterDouble;
import openperipheral.converter.ConverterFluidTankInfo;
import openperipheral.converter.ConverterForgeDirection;
import openperipheral.converter.ConverterItemStack;
import openperipheral.converter.ConverterList;
import openperipheral.integration.ModuleAppEng;
import openperipheral.integration.ModuleBuildCraft;
import openperipheral.integration.ModuleEnderStorage;
import openperipheral.integration.ModuleForestry;
import openperipheral.integration.ModuleIC2;
import openperipheral.integration.ModuleMystcraft;
import openperipheral.integration.ModuleProjectRed;
import openperipheral.integration.ModuleRailcraft;
import openperipheral.integration.ModuleSgCraft;
import openperipheral.integration.ModuleThaumcraft;
import openperipheral.integration.ModuleThermalExpansion;
import openperipheral.integration.ModuleVanilla;
import openperipheral.util.MountingUtils;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
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

		TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);

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
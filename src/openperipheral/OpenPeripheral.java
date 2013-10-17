package openperipheral;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import openperipheral.api.BlacklistRegistry;
import openperipheral.core.AdapterManager;
import openperipheral.core.BasicMount;
import openperipheral.core.CommonProxy;
import openperipheral.core.ConfigSettings;
import openperipheral.core.ConnectionHandler;
import openperipheral.core.Mods;
import openperipheral.core.PeripheralHandler;
import openperipheral.core.TickHandler;
import openperipheral.core.TypeConversionRegistry;
import openperipheral.core.adapter.AdapterGlassesBridge;
import openperipheral.core.adapter.AdapterObject;
import openperipheral.core.adapter.AdapterPlayerInventory;
import openperipheral.core.adapter.AdapterSensor;
import openperipheral.core.adapter.vanilla.AdapterFluidHandler;
import openperipheral.core.block.BlockPlayerInventory;
import openperipheral.core.block.BlockProxy;
import openperipheral.core.block.BlockTicketMachine;
import openperipheral.core.client.PacketHandler;
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
import openperipheral.core.integration.ModuleRailcraft;
import openperipheral.core.integration.ModuleThaumcraft;
import openperipheral.core.integration.ModuleVanilla;
import openperipheral.core.item.ItemGeneric;
import openperipheral.core.item.ItemGlasses;
import openperipheral.core.item.ItemRemote;
import openperipheral.core.item.ItemRobot;
import openperipheral.core.util.MountingUtils;
import openperipheral.glasses.block.BlockGlassesBridge;
import openperipheral.robots.RobotUpgradeManager;
import openperipheral.robots.block.BlockRobot;
import openperipheral.robots.upgrade.fuel.ProviderFuelUpgrade;
import openperipheral.robots.upgrade.inventory.ProviderInventoryUpgrade;
import openperipheral.robots.upgrade.laser.ProviderLasersUpgrade;
import openperipheral.robots.upgrade.movement.ProviderMovementUpgrade;
import openperipheral.robots.upgrade.sensor.ProviderSensorUpgrade;
import openperipheral.robots.upgrade.targeting.ProviderTargetingUpgrade;
import openperipheral.sensor.block.BlockSensor;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import dan200.computer.api.ComputerCraftAPI;
import dan200.computer.api.IPeripheral;

@Mod(
	modid = "OpenPeripheral",
	name = "OpenPeripheral",
	version = "@VERSION@",
	dependencies = "required-after:ComputerCraft;after:mmmPowersuits;after:BuildCraft|Core;after:AppliedEnergistics;after:Forestry;after:IC2;after:ThermalExpansion;after:Thaumcraft;after:MineFactoryReloaded;after:Railcraft;after:MiscPeripherals;after:Mystcraft")
@NetworkMod(serverSideRequired = true, clientSideRequired = false, channels = { ConfigSettings.NETWORK_CHANNEL }, packetHandler = PacketHandler.class)
public class OpenPeripheral {

	@Instance(value = "OpenPeripheral")
	public static OpenPeripheral instance;

	@SidedProxy(clientSide = "openperipheral.core.client.ClientProxy", serverSide = "openperipheral.core.CommonProxy")
	public static CommonProxy proxy;

	public static BasicMount mount = new BasicMount();

	public static CreativeTabs tabOpenPeripheral = new CreativeTabs("tabOpenPeripheral") {
		public ItemStack getIconItemStack() {
			return new ItemStack(OpenPeripheral.Items.glasses, 1, 0);
		}
	};

	public static class Items {
		public static ItemGlasses glasses;
		public static ItemRemote remote;
		public static ItemGeneric generic;
		public static ItemRobot robot;
	}

	public static class Blocks {
		public static BlockGlassesBridge glassesBridge;
		public static BlockProxy proxy;
		public static BlockPlayerInventory playerInventory;
		public static BlockTicketMachine ticketMachine;
		public static BlockSensor sensor;
		public static BlockRobot robot;
	}

	public enum Gui {
		ticketMachine, remote, robot, robotEntity
	};

	public static int renderId;

	public static PeripheralHandler peripheralHandler = new PeripheralHandler();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		ConfigSettings.loadAndSaveConfig(evt.getSuggestedConfigurationFile());
		MountingUtils.refreshLatestFiles();

	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent evt) {

		proxy.init();
		proxy.registerRenderInformation();

		TypeConversionRegistry.registerTypeConverter(new ConverterArray());
		TypeConversionRegistry.registerTypeConverter(new ConverterList());
		TypeConversionRegistry.registerTypeConverter(new ConverterDouble());
		TypeConversionRegistry.registerTypeConverter(new ConverterItemStack());
		TypeConversionRegistry.registerTypeConverter(new ConverterFluidTankInfo());
		TypeConversionRegistry.registerTypeConverter(new ConverterForgeDirection());
		TypeConversionRegistry.registerTypeConverter(new ConverterFluidTankInfo());
		
		AdapterManager.addPeripheralAdapter(new AdapterObject());
		AdapterManager.addPeripheralAdapter(new AdapterFluidHandler());
		AdapterManager.addPeripheralAdapter(new AdapterGlassesBridge());
		AdapterManager.addPeripheralAdapter(new AdapterSensor());
		AdapterManager.addPeripheralAdapter(new AdapterPlayerInventory());

		if (ConfigSettings.robotsEnabled) {
			RobotUpgradeManager.registerUpgradeProvider(new ProviderMovementUpgrade());
			RobotUpgradeManager.registerUpgradeProvider(new ProviderLasersUpgrade());
			RobotUpgradeManager.registerUpgradeProvider(new ProviderSensorUpgrade());
			RobotUpgradeManager.registerUpgradeProvider(new ProviderFuelUpgrade());
			RobotUpgradeManager.registerUpgradeProvider(new ProviderInventoryUpgrade());
			RobotUpgradeManager.registerUpgradeProvider(new ProviderTargetingUpgrade());
		}
		
		BlacklistRegistry.registerTileEntity(TileEntityCommandBlock.class);
		BlacklistRegistry.registerTileEntity(IPeripheral.class);
		
		ModuleVanilla.init();

		if (Loader.isModLoaded(Mods.APPLIED_ENERGISTICS)) {
			ModuleAppEng.init();
		}

		if (Loader.isModLoaded(Mods.IC2)) {
			ModuleIC2.init();
		}

		if (Loader.isModLoaded(Mods.ENDER_STORAGE)) {
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
		
		if (ModLoader.isModLoaded(Mods.RAILCRAFT)) {
			ModuleRailcraft.init();
		}
		
		if (ModLoader.isModLoaded(Mods.THAUMCRAFT)) {
			ModuleThaumcraft.init();
		}
		
		TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);
		ComputerCraftAPI.registerExternalPeripheral(TileEntity.class, peripheralHandler);
		NetworkRegistry.instance().registerConnectionHandler(new ConnectionHandler());
	}

}
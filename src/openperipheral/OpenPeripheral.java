package openperipheral;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IRestriction;
import openperipheral.api.RobotUpgradeManager;
import openperipheral.core.AdapterManager;
import openperipheral.core.CommonProxy;
import openperipheral.core.ConfigSettings;
import openperipheral.core.Mods;
import openperipheral.core.PeripheralHandler;
import openperipheral.core.TickHandler;
import openperipheral.core.block.BlockPlayerInventory;
import openperipheral.core.block.BlockProxy;
import openperipheral.core.block.BlockTicketMachine;
import openperipheral.core.client.PacketHandler;
import openperipheral.core.converter.ConverterArray;
import openperipheral.core.converter.ConverterDouble;
import openperipheral.core.converter.ConverterForgeDirection;
import openperipheral.core.converter.ConverterILiquidTank;
import openperipheral.core.converter.ConverterItemStack;
import openperipheral.core.converter.TypeConversionRegistry;
import openperipheral.core.integration.appliedenergistics.AEModule;
import openperipheral.core.integration.buildcraft.BCModule;
import openperipheral.core.integration.forestry.ForestryModule;
import openperipheral.core.integration.mps.MPSModule;
import openperipheral.core.integration.sgcraft.SGCraftModule;
import openperipheral.core.integration.thaumcraft.ThaumcraftModule;
import openperipheral.core.integration.thermalexpansion.TEModule;
import openperipheral.core.integration.vanilla.InventoryAdapter;
import openperipheral.core.interfaces.IRestrictionHandler;
import openperipheral.core.item.ItemGeneric;
import openperipheral.core.item.ItemGlasses;
import openperipheral.core.item.ItemRemote;
import openperipheral.core.item.ItemRobot;
import openperipheral.core.postchange.PostChangeMarkUpdate;
import openperipheral.core.postchange.PostChangeRegistry;
import openperipheral.core.postchange.PostChangeScript;
import openperipheral.core.restriction.RestrictionChoice;
import openperipheral.core.restriction.RestrictionFactory;
import openperipheral.core.restriction.RestrictionMaximum;
import openperipheral.core.restriction.RestrictionMinimum;
import openperipheral.core.util.MountingUtils;
import openperipheral.glasses.block.BlockGlassesBridge;
import openperipheral.robots.block.BlockRobot;
import openperipheral.robots.upgrade.fuel.ProviderFuelUpgrade;
import openperipheral.robots.upgrade.inventory.ProviderInventoryUpgrade;
import openperipheral.robots.upgrade.lazer.ProviderLazersUpgrade;
import openperipheral.robots.upgrade.movement.ProviderMovementUpgrade;
import openperipheral.robots.upgrade.sensor.ProviderSensorUpgrade;
import openperipheral.robots.upgrade.targeting.ProviderTargetingUpgrade;
import openperipheral.sensor.block.BlockSensor;
import argo.jdom.JsonNode;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import dan200.computer.api.ComputerCraftAPI;

@Mod(modid = "OpenPeripheral", name = "OpenPeripheral", version = "@VERSION@", dependencies = "required-after:ComputerCraft;after:mmmPowersuits;after:BuildCraft|Core;after:AppliedEnergistics;after:Forestry;after:IC2;after:ThermalExpansion;after:Thaumcraft;after:MineFactoryReloaded;after:Railcraft;after:MiscPeripherals")
@NetworkMod(serverSideRequired = true, clientSideRequired = false, channels = { ConfigSettings.NETWORK_CHANNEL }, packetHandler = PacketHandler.class)
public class OpenPeripheral {

	@Instance(value = "OpenPeripheral")
	public static OpenPeripheral instance;

	@SidedProxy(clientSide = "openperipheral.core.client.ClientProxy", serverSide = "openperipheral.core.CommonProxy")
	public static CommonProxy proxy;

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
		ticketMachine,
		remote,
		robot,
		robotEntity
	};

	public static int renderId;

	public static PeripheralHandler peripheralHandler = new PeripheralHandler();

	@Mod.PreInit
	public void preInit(FMLPreInitializationEvent evt) {
		ConfigSettings.loadAndSaveConfig(evt.getSuggestedConfigurationFile());
		MountingUtils.refreshLatestFiles();

	}

	@Mod.Init
	public void init(FMLInitializationEvent evt) {

		proxy.init();
		proxy.registerRenderInformation();

		RestrictionFactory.registerRestrictionHandler("min", new IRestrictionHandler() {
			@Override
			public IRestriction createFromJson(JsonNode json) {
				return new RestrictionMinimum(json);
			}
		});

		RestrictionFactory.registerRestrictionHandler("max", new IRestrictionHandler() {
			@Override
			public IRestriction createFromJson(JsonNode json) {
				return new RestrictionMaximum(json);
			}
		});

		RestrictionFactory.registerRestrictionHandler("choice", new IRestrictionHandler() {
			@Override
			public IRestriction createFromJson(JsonNode json) {
				return new RestrictionChoice(json);
			}
		});

		PostChangeRegistry.registerChangeHandler(new PostChangeMarkUpdate());
		PostChangeRegistry.registerChangeHandler(new PostChangeScript());

		TypeConversionRegistry.registryTypeConverter(new ConverterArray());
		TypeConversionRegistry.registryTypeConverter(new ConverterDouble());
		TypeConversionRegistry.registryTypeConverter(new ConverterItemStack());
		TypeConversionRegistry.registryTypeConverter(new ConverterILiquidTank());
		TypeConversionRegistry.registryTypeConverter(new ConverterForgeDirection());

		if (Loader.isModLoaded(Mods.APPLIED_ENERGISTICS)) {
			AEModule.init();
		}

		if (Loader.isModLoaded(Mods.FORESTRY)) {
			ForestryModule.init();
		}

		if (Loader.isModLoaded(Mods.BUILDCRAFT)) {
			BCModule.init();
		}

		if (Loader.isModLoaded(Mods.THAUMCRAFT)) {
			ThaumcraftModule.init();
		}

		if (Loader.isModLoaded(Mods.MPS)) {
			MPSModule.init();
		}

		if (Loader.isModLoaded(Mods.SGCRAFT)) {
			SGCraftModule.init();
		}
		
		if (Loader.isModLoaded(Mods.THERMALEXPANSION)) {
			TEModule.init();
		}
		
		AdapterManager.addPeripheralAdapter(new InventoryAdapter());
		
		if (ConfigSettings.robotsEnabled) {
			RobotUpgradeManager.registerUpgradeProvider(new ProviderMovementUpgrade());
			RobotUpgradeManager.registerUpgradeProvider(new ProviderLazersUpgrade());
			RobotUpgradeManager.registerUpgradeProvider(new ProviderSensorUpgrade());
			RobotUpgradeManager.registerUpgradeProvider(new ProviderFuelUpgrade());
			RobotUpgradeManager.registerUpgradeProvider(new ProviderInventoryUpgrade());
			RobotUpgradeManager.registerUpgradeProvider(new ProviderTargetingUpgrade());
		}
		
		TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);
		ComputerCraftAPI.registerExternalPeripheral(TileEntity.class, peripheralHandler);

	}

}
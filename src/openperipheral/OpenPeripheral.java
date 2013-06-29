package openperipheral;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IRestriction;
import openperipheral.api.IRestrictionHandler;
import openperipheral.client.PacketHandler;
import openperipheral.common.CommonProxy;
import openperipheral.common.block.BlockGlassesBridge;
import openperipheral.common.block.BlockPlayerInventory;
import openperipheral.common.block.BlockProxy;
import openperipheral.common.block.BlockSensor;
import openperipheral.common.block.BlockTicketMachine;
import openperipheral.common.config.ConfigSettings;
import openperipheral.common.converter.ConverterArray;
import openperipheral.common.converter.ConverterDouble;
import openperipheral.common.converter.ConverterForgeDirection;
import openperipheral.common.converter.ConverterILiquidTank;
import openperipheral.common.converter.ConverterItemStack;
import openperipheral.common.converter.TypeConversionRegistry;
import openperipheral.common.core.Mods;
import openperipheral.common.core.PeripheralHandler;
import openperipheral.common.core.TickHandler;
import openperipheral.common.definition.DefinitionManager;
import openperipheral.common.integration.appliedenergistics.AEModule;
import openperipheral.common.integration.buildcraft.BCModule;
import openperipheral.common.integration.forestry.ForestryModule;
import openperipheral.common.integration.gregtech.GregTechModule;
import openperipheral.common.integration.mps.MPSModule;
import openperipheral.common.integration.sgcraft.SGCraftModule;
import openperipheral.common.integration.thaumcraft.ThaumcraftModule;
import openperipheral.common.integration.thermalexpansion.TEModule;
import openperipheral.common.integration.vanilla.InventoryClassDefinition;
import openperipheral.common.item.ItemGeneric;
import openperipheral.common.item.ItemGlasses;
import openperipheral.common.item.ItemRemote;
import openperipheral.common.postchange.PostChangeMarkUpdate;
import openperipheral.common.postchange.PostChangeRegistry;
import openperipheral.common.postchange.PostChangeScript;
import openperipheral.common.restriction.RestrictionChoice;
import openperipheral.common.restriction.RestrictionFactory;
import openperipheral.common.restriction.RestrictionMaximum;
import openperipheral.common.restriction.RestrictionMinimum;
import openperipheral.common.util.MountingUtils;
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

	@SidedProxy(clientSide = "openperipheral.client.ClientProxy", serverSide = "openperipheral.common.CommonProxy")
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
	}

	public static class Blocks {
		public static BlockGlassesBridge glassesBridge;
		public static BlockProxy proxy;
		public static BlockPlayerInventory playerInventory;
		public static BlockTicketMachine ticketMachine;
		public static BlockSensor sensor;
	}

	public enum Gui {
		ticketMachine,
		remote
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
		
		if (Loader.isModLoaded(Mods.GREGTECH)) {
			GregTechModule.init();
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
		
		DefinitionManager.addClassDefinition(new InventoryClassDefinition());

		DefinitionManager.load();
		
		TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);
		ComputerCraftAPI.registerExternalPeripheral(TileEntity.class, peripheralHandler);

	}

}
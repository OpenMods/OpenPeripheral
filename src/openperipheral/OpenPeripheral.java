package openperipheral;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IRestriction;
import openperipheral.api.IRestrictionHandler;
import openperipheral.client.PacketHandler;
import openperipheral.common.CommonProxy;
import openperipheral.common.block.BlockGlassesBridge;
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
import openperipheral.common.integration.appliedenergistics.ConverterIMEInventory;
import openperipheral.common.integration.buildcraft.ConverterPowerProvider;
import openperipheral.common.integration.forestry.ConverterEnumHumidity;
import openperipheral.common.integration.forestry.ConverterEnumTemperature;
import openperipheral.common.integration.forestry.ConverterFruitFamily;
import openperipheral.common.integration.thaumcraft.ConverterEnumTag;
import openperipheral.common.integration.thaumcraft.ConverterObjectTags;
import openperipheral.common.item.ItemGlasses;
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

@Mod(modid = "OpenPeripheral", name = "OpenPeripheral", version = "0.1.6", dependencies = "required-after:ComputerCraft;after:mmmPowersuits;after:BuildCraft|Core;after:AppliedEnergistics;after:Forestry;after:IC2;after:ThermalExpansion;after:Thaumcraft;after:MineFactoryReloaded;after:Railcraft;after:MiscPeripherals")
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
	}

	public static class Blocks {
		
		public static BlockGlassesBridge glassesBridge;
	}

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
			TypeConversionRegistry.registryTypeConverter(new ConverterIMEInventory());
		}

		if (Loader.isModLoaded(Mods.FORESTRY)) {
			TypeConversionRegistry.registryTypeConverter(new ConverterEnumHumidity());
			TypeConversionRegistry.registryTypeConverter(new ConverterEnumTemperature());
			TypeConversionRegistry.registryTypeConverter(new ConverterFruitFamily());
		}

		if (Loader.isModLoaded(Mods.BUILDCRAFT)) {
			TypeConversionRegistry.registryTypeConverter(new ConverterPowerProvider());
		}

		if (Loader.isModLoaded(Mods.THAUMCRAFT)) {
			TypeConversionRegistry.registryTypeConverter(new ConverterObjectTags());
			TypeConversionRegistry.registryTypeConverter(new ConverterEnumTag());
		}

		DefinitionManager.load();

		TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);
		ComputerCraftAPI.registerExternalPeripheral(TileEntity.class, new PeripheralHandler());

	}

}
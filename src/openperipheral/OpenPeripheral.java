package openperipheral;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import openperipheral.common.CommonProxy;
import openperipheral.common.block.BlockGlassesBridge;
import openperipheral.common.item.ItemGlasses;
import openperipheral.common.terminal.DrawableManager;
import openperipheral.common.util.PacketChunker;
import openperipheral.converter.ConverterArray;
import openperipheral.converter.ConverterDouble;
import openperipheral.converter.ConverterForgeDirection;
import openperipheral.converter.ConverterILiquidTank;
import openperipheral.converter.ConverterItemStack;
import openperipheral.converter.appliedenergistics.ConverterIMEInventory;
import openperipheral.converter.buildcraft.ConverterPowerProvider;
import openperipheral.converter.forestry.ConverterEnumHumidity;
import openperipheral.converter.forestry.ConverterEnumTemperature;
import openperipheral.converter.forestry.ConverterFruitFamily;
import openperipheral.converter.thaumcraft.ConverterEnumTag;
import openperipheral.converter.thaumcraft.ConverterObjectTags;
import openperipheral.definition.DefinitionClass;
import openperipheral.definition.DefinitionMethod;
import openperipheral.definition.DefinitionMod;
import openperipheral.definition.ModList;
import openperipheral.postchange.PostChangeMarkUpdate;
import openperipheral.postchange.PostChangeScript;
import openperipheral.restriction.RestrictionChoice;
import openperipheral.restriction.RestrictionMaximum;
import openperipheral.restriction.RestrictionMinimum;
import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.FMLRelauncher;
import cpw.mods.fml.relauncher.Side;
import dan200.computer.api.ComputerCraftAPI;

@Mod(modid = "OpenPeripheral", name = "OpenPeripheral", version = "0.1.5", dependencies = "required-after:ComputerCraft;after:BuildCraft|Core;after:AppliedEnergistics;after:Forestry;after:IC2;after:ThermalExpansion;after:Thaumcraft;after:MineFactoryReloaded;after:Railcraft;after:MiscPeripherals")
@NetworkMod(serverSideRequired = true, clientSideRequired = false, channels = { "OpenPeripheral" }, packetHandler = PacketHandler.class)
public class OpenPeripheral {

	public static ModList definitions = new ModList();
	public static HashMap<Class, DefinitionClass> classList = new HashMap<Class, DefinitionClass>();

	@Instance(value = "OpenPeripheral")
	public static OpenPeripheral instance;

	@SidedProxy(clientSide = "openperipheral.client.ClientProxy", serverSide = "openperipheral.common.CommonProxy")
	public static CommonProxy proxy;

	public static String CACHE_FILE = "OpenPeripheral_methods.json";
	public static String CACHE_PATH = "";
	public static String DATA_URL = "https://raw.github.com/mikeemoo/OpenPeripheral/master/methods_new.json";
	// public static String DATA_URL = "http://localhost/methods_new.json";
	public static int CACHE_REFRESH_INTERVAL = 7;
	public static boolean analyticsEnabled = true;
	public static boolean doAnalytics = false;
	public static String previousVersion;

	
	public static CreativeTabs tabOpenPeripheral = new CreativeTabs("tabOpenPeripheral") {
        public ItemStack getIconItemStack() {
                return new ItemStack(OpenPeripheral.Items.glasses, 1, 0);
        }
	};

	public static int getHoliday() {
		Calendar today = Calendar.getInstance();
		int month = today.get(2);
		int day = today.get(5);
		if ((month == 1) && (day == 14)) {
			return 1;
		}
		if ((month == 9) && (day == 31)) {
			return 2;
		}
		if ((month == 11) && (day >= 24) && (day <= 30)) {
			return 3;
		}
		return 0;
	}

	public static class Items {
		public static ItemGlasses glasses;
	}

	public static class Blocks {
		public static BlockGlassesBridge glassesBridge;
	}

	public static class Config {
		public static int glassesId = 1055;
		public static int glassesBridgeId = 580;
	}

	public static String RESOURCE_PATH;
	public static String LANGUAGE_PATH;

	@Mod.PreInit
	public void preInit(FMLPreInitializationEvent evt) {
		
		RESOURCE_PATH = "/mods/openperipheral";
		LANGUAGE_PATH = String.format("%s/languages", RESOURCE_PATH);

		Configuration configFile = new Configuration(
				evt.getSuggestedConfigurationFile());

		ModContainer container = FMLCommonHandler.instance().findContainerFor(
				OpenPeripheral.instance);
		String version = container.getVersion();

		Property prop = configFile.get("general", "enableAnalytics", true);
		prop.comment = "Do you want analytics enabled?";
		analyticsEnabled = prop.getBoolean(true);

		prop = configFile.get("general", "currentVersion", 0);
		prop.comment = "Do not edit this file. OpenPeripheral adds no blocks or items! this value should not be changed";
		previousVersion = prop.getString();

		if (version != previousVersion) {
			doAnalytics = true;
		}

		prop.set(version);

		prop = configFile.get("general", "dataUrl", DATA_URL);
		prop.comment = "The URL of the data file";
		DATA_URL = prop.getString();

		prop = configFile.get("general", "cacheFile", CACHE_FILE);
		prop.comment = "The path to the cache file";
		CACHE_FILE = prop.getString();

		prop = configFile.get("general", "cacheInterval", CACHE_REFRESH_INTERVAL);
		prop.comment = "How often the cache file gets updated (in days)";
		CACHE_REFRESH_INTERVAL = prop.getInt();

		prop = configFile.get("items", "glassesId", Config.glassesId);
		prop.comment = "The id of the glasses";
		Config.glassesId = prop.getInt();

		prop = configFile.get("blocks", "bridgeId", Config.glassesBridgeId);
		prop.comment = "The id of the glasses bridge";
		Config.glassesBridgeId = prop.getInt();

		if (doAnalytics && analyticsEnabled) {
			analytics(container);
		}

		File directory = null;
		if (FMLRelauncher.side() == "CLIENT") {
			directory = new File(Minecraft.getMinecraftDir(), "config/");
		} else {
			directory = new File(".", "config/");
		}
		File cacheFile = new File(directory, CACHE_FILE);

		configFile.save();
		CACHE_PATH = cacheFile.getAbsolutePath();
	}

	private void analytics(ModContainer container) {
		String charset = "UTF-8";
		String url;
		try {
			url = String
					.format("http://www.openccsensors.info/op_analytics?version=%s&side=%s&forge=%s",
							URLEncoder.encode(container.getVersion(), charset),
							URLEncoder.encode(FMLRelauncher.side(), charset),
							URLEncoder.encode(ForgeVersion.getVersion(),
									charset));
			URLConnection connection = new URL(url).openConnection();
			connection.setConnectTimeout(4000);
			connection.setRequestProperty("Accept-Charset", charset);
			InputStream response = connection.getInputStream();
		} catch (Exception e) {
		}
	}

	@Mod.Init
	public void init(FMLInitializationEvent evt) {

		proxy.init();
		proxy.registerRenderInformation();

		RestrictionFactory.registerRestrictionHandler("min",
				new IRestrictionHandler() {
					@Override
					public IRestriction createFromJson(JsonNode json) {
						return new RestrictionMinimum(json);
					}
				});

		RestrictionFactory.registerRestrictionHandler("max",
				new IRestrictionHandler() {
					@Override
					public IRestriction createFromJson(JsonNode json) {
						return new RestrictionMaximum(json);
					}
				});

		RestrictionFactory.registerRestrictionHandler("choice",
				new IRestrictionHandler() {
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
		TypeConversionRegistry
				.registryTypeConverter(new ConverterILiquidTank());
		TypeConversionRegistry
				.registryTypeConverter(new ConverterForgeDirection());

		if (ModLoader.isModLoaded(Mods.APPLIED_ENERGISTICS)) {
			TypeConversionRegistry
					.registryTypeConverter(new ConverterIMEInventory());
		}
		if (ModLoader.isModLoaded(Mods.FORESTRY)) {
			TypeConversionRegistry
					.registryTypeConverter(new ConverterEnumHumidity());
			TypeConversionRegistry
					.registryTypeConverter(new ConverterEnumTemperature());
			TypeConversionRegistry
					.registryTypeConverter(new ConverterFruitFamily());
		}
		if (ModLoader.isModLoaded(Mods.BUILDCRAFT)) {
			TypeConversionRegistry
					.registryTypeConverter(new ConverterPowerProvider());
		}
		if (ModLoader.isModLoaded(Mods.THAUMCRAFT)) {
			TypeConversionRegistry
					.registryTypeConverter(new ConverterObjectTags());
			TypeConversionRegistry
					.registryTypeConverter(new ConverterEnumTag());
		}

		JsonRootNode rootNode = loadJSON();

		if (rootNode != null) {
			for (JsonNode modNode : rootNode.getElements()) {
				DefinitionMod definition = new DefinitionMod(modNode);
				classList.putAll(definition.getValidClasses());
			}
		}

		TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);
		ComputerCraftAPI.registerExternalPeripheral(TileEntity.class,
				new PeripheralHandler());

	}

	public static ArrayList<DefinitionMethod> getMethodsForClass(Class klass) {

		ArrayList<DefinitionMethod> methods = new ArrayList<DefinitionMethod>();
		for (Entry<Class, DefinitionClass> entry : classList.entrySet()) {
			if (entry.getKey().isAssignableFrom(klass)) {
				methods.addAll(entry.getValue().getMethods());
			}
		}

		return methods;
	}

	private JsonRootNode loadJSON() {

		File file = new File(OpenPeripheral.CACHE_PATH);
		if (!file.exists()) {
			fetchNewData();
		} else if (file.lastModified() < System.currentTimeMillis()
				- (CACHE_REFRESH_INTERVAL * 24 * 60 * 60 * 1000)) {
			fetchNewData();
		}

		try {
			System.out.println("Parsing openperipheral json");
			BufferedReader br = new BufferedReader(new FileReader(
					OpenPeripheral.CACHE_PATH));
			JdomParser parser = new JdomParser();
			JsonRootNode root = parser.parse(br);
			return root;
		} catch (Exception e) {
			System.out.println("Unable to parse openperipherals");
		}

		return null;
	}

	private void fetchNewData() {
		System.out.println("Fetching new openperipherals data from "
				+ OpenPeripheral.DATA_URL);
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(
					new URL(OpenPeripheral.DATA_URL).openStream());
			fout = new FileOutputStream(OpenPeripheral.CACHE_PATH);

			byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) {
				fout.write(data, 0, count);
			}
		} catch (Exception e) {
			System.out.println("Error fetching openperipheral data");
		}

		try {
			if (in != null)
				in.close();
			if (fout != null)
				fout.close();
		} catch (IOException e) {
		}
	}

}
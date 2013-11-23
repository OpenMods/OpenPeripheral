package openperipheral;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.FMLLaunchHandler;

public class ConfigSettings {
	private static final String GENERAL = "general";
	
	public static int CACHE_REFRESH_INTERVAL = 7;
	public static String RESOURCE_PATH = "/assets/openperipheral";
	public static String LANGUAGE_PATH = String.format("%s/languages", RESOURCE_PATH);
	public static String TEXTURES_PATH = String.format("%s/textures", RESOURCE_PATH);

	private static String externalBase = "https://raw.github.com/mikeemoo/OpenPeripheral/master/";

	public static String EXTERNAL_LUA_LISTING = String.format("%s%s", externalBase, "assets/openperipheral/scripts.txt");
	public static String EXTERNAL_LUA_FOLDER = String.format("%s%s", externalBase, "assets/openperipheral/lua/");
	public static String LOCAL_LUA_LISTING;
	public static String LOCAL_LUA_FOLDER;

	public static boolean FRESH_INSTALL = false;

	public static boolean analyticsEnabled = true;

	public static String CACHE_FILE = "OpenPeripheral_methods.json";
	public static String CACHE_PATH = "";
	public static String DATA_URL = String.format("%s%s", externalBase, "methods_new.json");
	public static String previousVersion;

	public static boolean enabledExtendedInventory = true;

	public static int cellsPerRedstone = 3;

	public static void loadAndSaveConfig(File suggestedConfigFile) {
		Configuration configFile = new Configuration(suggestedConfigFile);

		ModContainer container = FMLCommonHandler.instance().findContainerFor(OpenPeripheral.instance);
		String version = container.getVersion();

		Property prop = configFile.get(GENERAL, "cellsPerRedstone", cellsPerRedstone);
		prop.comment = "How many cells are crafted per redstone? Use this to balance";
		cellsPerRedstone = prop.getInt();

		prop = configFile.get(GENERAL, "enableAnalytics", true);
		prop.comment = "Do you want analytics enabled?";
		analyticsEnabled = prop.getBoolean(true);

		prop = configFile.get(GENERAL, "currentVersion", 0);
		prop.comment = "You do not need to change this";
		previousVersion = prop.getString();

		if (version != previousVersion) {
			FRESH_INSTALL = true;
		}

		prop.set(version);

		prop = configFile.get(GENERAL, "dataUrl", DATA_URL);
		prop.comment = "The URL of the data file";
		DATA_URL = prop.getString();

		prop = configFile.get(GENERAL, "cacheFile", CACHE_FILE);
		prop.comment = "The path to the cache file";
		CACHE_FILE = prop.getString();

		prop = configFile.get(GENERAL, "cacheInterval", CACHE_REFRESH_INTERVAL);
		prop.comment = "How often the cache file gets updated (in days)";
		CACHE_REFRESH_INTERVAL = prop.getInt();

		prop = configFile.get(GENERAL, "enabledExtendedInventory", enabledExtendedInventory);
		prop.comment = "Do you wish to enable the extended inventory methods? (pull, push, swap)";
		enabledExtendedInventory = prop.getBoolean(enabledExtendedInventory);

		if (FRESH_INSTALL && analyticsEnabled) {
			analytics(container);
		}

		File baseDirectory = null;

		if (FMLLaunchHandler.side().isClient()) {
			baseDirectory = Minecraft.getMinecraft().mcDataDir;
		} else {
			baseDirectory = new File(".");
		}

		File configDirectory = new File(baseDirectory, "config/");
		File cacheFile = new File(configDirectory, CACHE_FILE);
		File openPeripheralFolder = new File(baseDirectory, "openperipheral/");
		File luaFolder = new File(openPeripheralFolder, "lua/");

		LOCAL_LUA_LISTING = openPeripheralFolder.getAbsolutePath() + "/scripts.txt";
		LOCAL_LUA_FOLDER = luaFolder.getAbsolutePath();

		configFile.save();
		CACHE_PATH = cacheFile.getAbsolutePath();
	}

	private static void analytics(ModContainer container) {
		String charset = "UTF-8";
		String url;
		try {
			url = String.format("http://www.openccsensors.info/op_analytics?version=%s&side=%s&forge=%s", URLEncoder.encode(container.getVersion(), charset), URLEncoder.encode(FMLLaunchHandler.side().name(), charset), URLEncoder.encode(ForgeVersion.getVersion(), charset));
			URLConnection connection = new URL(url).openConnection();
			connection.setConnectTimeout(4000);
			connection.setRequestProperty("Accept-Charset", charset);
			// InputStream response = connection.getInputStream();
		} catch (Exception e) {}
	}
}

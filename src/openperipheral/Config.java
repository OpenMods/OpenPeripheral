package openperipheral;

import openmods.config.ConfigProperty;

public class Config {

	@ConfigProperty(category = "integration", name = "disableMods", comment = "Disable integration with mods identified by modid, even when they are loaded")
	public static String[] blacklist = new String[0];

	@ConfigProperty(category = "dev", name = "enableDevMethods", comment = "Enable methods that access reflection metadata")
	public static boolean devMethods = false;
}

package openperipheral;

import openmods.config.ConfigProperty;

public class Config {

	@ConfigProperty(category = "integration", name = "disableMods", comment = "Disable integration with mods identified by modid, even when they are loaded")
	public static String[] modBlacklist = new String[0];

	@ConfigProperty(category = "integration", name = "disableClasses", comment = "Don't register OpenPeripheral handler for those Tile Entitites (either name or class)")
	public static String[] teBlacklist = new String[0];

	@ConfigProperty(category = "dev", name = "enableDevMethods", comment = "Enable methods that access reflection metadata")
	public static boolean devMethods = false;
}

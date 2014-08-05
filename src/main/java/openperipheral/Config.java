package openperipheral;

import openmods.config.properties.ConfigProperty;

public class Config {

	@ConfigProperty(category = "integration", name = "disableClasses", comment = "Don't register OpenPeripheral handler for those Tile Entitites (either name or class)")
	public static String[] teBlacklist = new String[0];

	@ConfigProperty(category = "dev", name = "enableDevMethods", comment = "Enable methods that access reflection metadata")
	public static boolean devMethods = false;
}

package openperipheral;

import openmods.config.properties.ConfigProperty;
import openmods.config.properties.OnLineModifiable;

public class Config {

	@OnLineModifiable
	@ConfigProperty(category = "integration", name = "disableClasses", comment = "Don't register OpenPeripheral handler for those Tile Entitites (either name or class)")
	public static String[] teBlacklist = new String[0];

	@ConfigProperty(category = "dev", name = "enableDevMethods", comment = "Enable methods that access reflection metadata")
	public static boolean devMethods = false;

	@ConfigProperty(category = "performance", name = "threadPoolForSignallingCalls", comment = "Number of threads available to calls marked with @ReturnSignal")
	public static int signallingPoolSize = 10;
}

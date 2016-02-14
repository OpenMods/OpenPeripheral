package openperipheral;

import openmods.config.properties.ConfigProperty;

public class Config {

	public static final String CATEGORY_FEATURE_GROUPS = "featureGroups";
	public static final String FIELD_FEATURE_GROUPS = "blacklist";

	@ConfigProperty(category = CATEGORY_FEATURE_GROUPS, name = FIELD_FEATURE_GROUPS, comment = "Blacklist for feature groups. Also configurable by GUI. Note: to get names of feature groups, wrap peripheral at least once and then use either /op_dump or config GUI")
	public static String[] featureGroupsBlacklist = new String[0];

	@ConfigProperty(category = "integration", name = "disableClasses", comment = "Don't register OpenPeripheral handler for those Tile Entitites (either name or class)")
	public static String[] teBlacklist = new String[0];

	@ConfigProperty(category = "dev", name = "enableDevMethods", comment = "Enable methods that access reflection metadata")
	public static boolean devMethods = false;

	@ConfigProperty(category = "performance", name = "threadPoolForSignallingCalls", comment = "Number of threads available to calls marked with @ReturnSignal")
	public static int signallingPoolSize = 10;

	@ConfigProperty(category = "interfaces", name = "ComputerCraft", comment = "Controls ComputerCraft integration")
	public static boolean interfaceComputerCraft = true;

	@ConfigProperty(category = "interfaces", name = "OpenComputers", comment = "Controls OpenComputers integration")
	public static boolean interfaceOpenComputers = true;
}

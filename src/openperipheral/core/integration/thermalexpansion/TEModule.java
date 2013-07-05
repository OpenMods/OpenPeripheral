package openperipheral.core.integration.thermalexpansion;

import openperipheral.core.definition.DefinitionManager;

public class TEModule {
	
	public static String[] tesseractModes = new String[] {
		"SEND",
		"RECEIVE",
		"BOTH"
	};
	
	public static void init() {
		DefinitionManager.addClassDefinition(new TesseractClassDefinition());
	}
}

package openperipheral.common.integration.thermalexpansion;

import openperipheral.common.definition.DefinitionManager;

public class TEModule {

	public static void init() {
		DefinitionManager.addClassDefinition(new TesseractClassDefinition());
	}
}

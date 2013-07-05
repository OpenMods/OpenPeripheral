package openperipheral.core.integration.gregtech;

import openperipheral.core.definition.DefinitionManager;

public class GregTechModule {

	public static void init() {
		DefinitionManager.addClassDefinition(new DefinitionTeleporter());
	}

}

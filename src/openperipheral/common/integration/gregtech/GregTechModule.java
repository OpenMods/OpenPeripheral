package openperipheral.common.integration.gregtech;

import openperipheral.common.definition.DefinitionManager;

public class GregTechModule {

	public static void init() {
		DefinitionManager.addClassDefinition(new DefinitionTeleporter());
	}

}

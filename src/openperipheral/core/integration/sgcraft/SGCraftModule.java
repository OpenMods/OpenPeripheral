package openperipheral.core.integration.sgcraft;

import openperipheral.core.definition.DefinitionManager;

public class SGCraftModule {

	public static void init() {
		DefinitionManager.addClassDefinition(new DefinitionBaseSGTileClass());
	}
	
}

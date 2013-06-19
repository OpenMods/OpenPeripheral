package openperipheral.common.integration.sgcraft;

import openperipheral.common.definition.DefinitionManager;

public class SGCraftModule {

	public static void init() {
		DefinitionManager.addClassDefinition(new DefinitionBaseSGTileClass());
	}
	
}

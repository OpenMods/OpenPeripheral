package openperipheral.common.integration.sgcraft;

import openperipheral.common.definition.DefinitionManager;

public class SGCraftModule {

	public static void init() {

		DefinitionBaseSGTileClass stargate = new DefinitionBaseSGTileClass();
		if (stargate.getJavaClass() != null) {
			DefinitionManager.addClass(stargate.getJavaClass(), stargate);
		}
	}
	
}

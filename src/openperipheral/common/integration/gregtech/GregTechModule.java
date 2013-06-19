package openperipheral.common.integration.gregtech;

import openperipheral.common.definition.DefinitionManager;

public class GregTechModule {

	public static void init() {
		DefinitionTeleporter teleporter = new DefinitionTeleporter();
		if (teleporter.getJavaClass() != null) {
			DefinitionManager.addClass(teleporter.getJavaClass(), teleporter);
		}
	}

}

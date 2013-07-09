package openperipheral.core.integration.sgcraft;

import openperipheral.core.AdapterManager;

public class SGCraftModule {

	public static void init() {
		AdapterManager.addPeripheralAdapter(new SGTileAdapter());
	}
	
}

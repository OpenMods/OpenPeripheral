package openperipheral.core.integration;

import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.sgcraft.AdapterStargate;

public class ModuleSgCraft {
	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterStargate());
	}
}

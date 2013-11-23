package openperipheral.integration;

import openperipheral.AdapterManager;
import openperipheral.adapter.sgcraft.AdapterStargate;

public class ModuleSgCraft {
	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterStargate());
	}
}

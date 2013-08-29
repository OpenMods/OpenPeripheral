package openperipheral.core.integration;

import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.buildcraft.AdapterPowerReceptor;

public class ModuleBuildCraft {

	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterPowerReceptor());
	}
}

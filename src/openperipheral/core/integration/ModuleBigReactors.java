package openperipheral.core.integration;

import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.bigreactors.AdapterHeatEntity;

public class ModuleBigReactors {
	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterHeatEntity());
	}

}

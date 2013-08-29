package openperipheral.core.integration;

import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.enderstorage.AdapterFrequencyOwner;

public class ModuleEnderStorage {

	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterFrequencyOwner());
	}
}

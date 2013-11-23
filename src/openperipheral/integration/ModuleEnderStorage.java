package openperipheral.integration;

import openperipheral.AdapterManager;
import openperipheral.adapter.enderstorage.AdapterFrequencyOwner;

public class ModuleEnderStorage {

	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterFrequencyOwner());
	}
}

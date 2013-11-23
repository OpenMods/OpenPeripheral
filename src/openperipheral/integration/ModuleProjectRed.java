package openperipheral.integration;

import openperipheral.AdapterManager;
import openperipheral.adapter.projectred.AdapterBundledCablePart;
import openperipheral.adapter.projectred.AdapterInsulatedRedwirePart;

public class ModuleProjectRed {
	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterBundledCablePart());
		AdapterManager.addPeripheralAdapter(new AdapterInsulatedRedwirePart());
	}

}

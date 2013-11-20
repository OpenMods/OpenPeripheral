package openperipheral.core.integration;

import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.projectred.AdapterBundledCablePart;
import openperipheral.core.adapter.projectred.AdapterInsulatedRedwirePart;

public class ModuleProjectRed {
	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterBundledCablePart());
		AdapterManager.addPeripheralAdapter(new AdapterInsulatedRedwirePart());
	}

}

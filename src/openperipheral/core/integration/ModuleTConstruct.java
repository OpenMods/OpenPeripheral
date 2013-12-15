package openperipheral.core.integration;

import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.tconstruct.AdapterDrawbridgeLogicBase;

public class ModuleTConstruct {
	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterDrawbridgeLogicBase());
	}
}

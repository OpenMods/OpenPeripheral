package openperipheral.core.integration.thermalexpansion;

import openperipheral.core.AdapterManager;

public class TEModule {
	
	public static String[] tesseractModes = new String[] {
		"SEND",
		"RECEIVE",
		"BOTH"
	};
	
	public static void init() {
		AdapterManager.addPeripheralAdapter(new TesseractAdapter());
	}
}

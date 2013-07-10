package openperipheral.core.integration;

import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.AdapterCellProvider;
import openperipheral.core.adapter.AdapterGridInterface;
import openperipheral.core.converter.ConverterIAEItemStack;
import openperipheral.core.converter.TypeConversionRegistry;

public class ModuleAppEng {

	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterCellProvider());
		AdapterManager.addPeripheralAdapter(new AdapterGridInterface());
		TypeConversionRegistry.registerTypeConverter(new ConverterIAEItemStack());
	}
	
}

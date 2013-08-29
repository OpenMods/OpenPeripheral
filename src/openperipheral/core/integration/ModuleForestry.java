package openperipheral.core.integration;

import openperipheral.core.AdapterManager;
import openperipheral.core.TypeConversionRegistry;
import openperipheral.core.adapter.forestry.AdapterBeeHousing;
import openperipheral.core.converter.ConverterIIndividual;

public class ModuleForestry {
	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterBeeHousing());
		TypeConversionRegistry.registerTypeConverter(new ConverterIIndividual());
	}
}

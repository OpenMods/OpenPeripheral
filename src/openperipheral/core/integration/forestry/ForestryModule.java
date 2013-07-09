package openperipheral.core.integration.forestry;

import openperipheral.core.AdapterManager;
import openperipheral.core.converter.TypeConversionRegistry;

public class ForestryModule {

	public static void init() {
		TypeConversionRegistry.registryTypeConverter(new ConverterEnumHumidity());
		TypeConversionRegistry.registryTypeConverter(new ConverterEnumTemperature());
		TypeConversionRegistry.registryTypeConverter(new ConverterFruitFamily());
		AdapterManager.addPeripheralAdapter(new ApiaristChestAdapter());
		AdapterManager.addPeripheralAdapter(new BeeHousingAdapter());
	}

}

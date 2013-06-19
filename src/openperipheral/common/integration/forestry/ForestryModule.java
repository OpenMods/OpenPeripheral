package openperipheral.common.integration.forestry;

import openperipheral.common.converter.TypeConversionRegistry;

public class ForestryModule {

	public static void init() {
		TypeConversionRegistry.registryTypeConverter(new ConverterEnumHumidity());
		TypeConversionRegistry.registryTypeConverter(new ConverterEnumTemperature());
		TypeConversionRegistry.registryTypeConverter(new ConverterFruitFamily());
	}

}

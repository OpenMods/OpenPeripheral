package openperipheral.common.integration.buildcraft;

import openperipheral.common.converter.TypeConversionRegistry;

public class BCModule {

	public static void init() {
		TypeConversionRegistry.registryTypeConverter(new ConverterPowerProvider());
	}
}

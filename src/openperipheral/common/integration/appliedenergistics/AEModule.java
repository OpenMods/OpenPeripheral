package openperipheral.common.integration.appliedenergistics;

import openperipheral.common.converter.TypeConversionRegistry;

public class AEModule {

	public static void init() {
		TypeConversionRegistry.registryTypeConverter(new ConverterIMEInventory());
	}

}

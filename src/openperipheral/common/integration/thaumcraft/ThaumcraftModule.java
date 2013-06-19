package openperipheral.common.integration.thaumcraft;

import openperipheral.common.converter.TypeConversionRegistry;

public class ThaumcraftModule {

	public static void init() {
		TypeConversionRegistry.registryTypeConverter(new ConverterObjectTags());
		TypeConversionRegistry.registryTypeConverter(new ConverterEnumTag());
	}

}

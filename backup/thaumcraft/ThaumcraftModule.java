package openperipheral.core.integration.thaumcraft;

import openperipheral.core.converter.TypeConversionRegistry;

public class ThaumcraftModule {

	public static void init() {
		TypeConversionRegistry.registryTypeConverter(new ConverterObjectTags());
		TypeConversionRegistry.registryTypeConverter(new ConverterEnumTag());
	}

}

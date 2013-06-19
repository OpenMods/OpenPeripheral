package openperipheral.common.integration.buildcraft;

import openperipheral.common.converter.TypeConversionRegistry;
import openperipheral.common.definition.DefinitionManager;

public class BCModule {

	public static void init() {
		TypeConversionRegistry.registryTypeConverter(new ConverterPowerProvider());
		DefinitionManager.addClassDefinition(new DefinitionPowerProviderClass());
	}
}

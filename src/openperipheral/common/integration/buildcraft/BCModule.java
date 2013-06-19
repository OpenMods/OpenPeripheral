package openperipheral.common.integration.buildcraft;

import openperipheral.common.converter.TypeConversionRegistry;
import openperipheral.common.definition.DefinitionManager;
import openperipheral.common.integration.buildcraft.engine.DefinitionEngineClass;
import openperipheral.common.integration.buildcraft.powerprovider.ConverterPowerProvider;
import openperipheral.common.integration.buildcraft.powerprovider.DefinitionPowerProviderClass;

public class BCModule {

	public static void init() {
		TypeConversionRegistry.registryTypeConverter(new ConverterPowerProvider());
		DefinitionManager.addClassDefinition(new DefinitionPowerProviderClass());
		DefinitionManager.addClassDefinition(new DefinitionEngineClass());
	}
}

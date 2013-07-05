package openperipheral.core.integration.buildcraft;

import openperipheral.core.converter.TypeConversionRegistry;
import openperipheral.core.definition.DefinitionManager;
import openperipheral.core.integration.buildcraft.engine.DefinitionEngineClass;
import openperipheral.core.integration.buildcraft.powerprovider.ConverterPowerProvider;
import openperipheral.core.integration.buildcraft.powerprovider.DefinitionPowerProviderClass;

public class BCModule {

	public static void init() {
		TypeConversionRegistry.registryTypeConverter(new ConverterPowerProvider());
		DefinitionManager.addClassDefinition(new DefinitionPowerProviderClass());
		DefinitionManager.addClassDefinition(new DefinitionEngineClass());
	}
}

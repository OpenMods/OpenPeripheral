package openperipheral.core.integration.appliedenergistics;

import openperipheral.core.converter.TypeConversionRegistry;
import openperipheral.core.definition.DefinitionManager;
import openperipheral.core.integration.appliedenergistics.cellprovider.DefinitionCellProviderClass;
import openperipheral.core.integration.appliedenergistics.gridinterface.DefinitionGridInterfaceClass;

public class AEModule {

	public static void init() {
		TypeConversionRegistry.registryTypeConverter(new ConverterIMEInventory());
		DefinitionManager.addClassDefinition(new DefinitionGridInterfaceClass());
		DefinitionManager.addClassDefinition(new DefinitionCellProviderClass());
	}

}

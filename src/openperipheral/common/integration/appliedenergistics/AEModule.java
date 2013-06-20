package openperipheral.common.integration.appliedenergistics;

import openperipheral.common.converter.TypeConversionRegistry;
import openperipheral.common.definition.DefinitionManager;
import openperipheral.common.integration.appliedenergistics.cellprovider.DefinitionCellProviderClass;
import openperipheral.common.integration.appliedenergistics.gridinterface.DefinitionGridInterfaceClass;

public class AEModule {

	public static void init() {
		TypeConversionRegistry.registryTypeConverter(new ConverterIMEInventory());
		DefinitionManager.addClassDefinition(new DefinitionGridInterfaceClass());
		DefinitionManager.addClassDefinition(new DefinitionCellProviderClass());
	}

}

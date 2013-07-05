package openperipheral.core.integration.forestry;

import openperipheral.core.converter.TypeConversionRegistry;
import openperipheral.core.definition.DefinitionManager;

public class ForestryModule {

	public static void init() {
		TypeConversionRegistry.registryTypeConverter(new ConverterEnumHumidity());
		TypeConversionRegistry.registryTypeConverter(new ConverterEnumTemperature());
		TypeConversionRegistry.registryTypeConverter(new ConverterFruitFamily());
		DefinitionManager.addClassDefinition(new GenericBeeInfoClassDefinition("forestry.apiculture.gadgets.TileApiaristChest"));
		DefinitionManager.addClassDefinition(new GenericBeeInfoClassDefinition("forestry.apiculture.gadgets.TileAlvearyPlain"));
		DefinitionManager.addClassDefinition(new BeeHousingClassDefinition());
	}

}

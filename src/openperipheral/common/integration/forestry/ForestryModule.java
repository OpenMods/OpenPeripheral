package openperipheral.common.integration.forestry;

import openperipheral.common.converter.TypeConversionRegistry;
import openperipheral.common.definition.DefinitionManager;

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

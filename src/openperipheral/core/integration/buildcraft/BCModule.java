package openperipheral.core.integration.buildcraft;

import openperipheral.core.AdapterManager;
import openperipheral.core.converter.TypeConversionRegistry;

public class BCModule {

	public static void init() {
		TypeConversionRegistry.registryTypeConverter(new ConverterPowerProvider());
		AdapterManager.addPeripheralAdapter(new PowerProviderAdapter());
		AdapterManager.addPeripheralAdapter(new EngineAdapter());
	}
}

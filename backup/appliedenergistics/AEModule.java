package openperipheral.core.integration.appliedenergistics;

import openperipheral.core.AdapterManager;
import openperipheral.core.converter.TypeConversionRegistry;
public class AEModule {

	public static void init() {
		TypeConversionRegistry.registryTypeConverter(new ConverterIMEInventory());
		AdapterManager.addPeripheralAdapter(new GridInterfaceAdapter());
		AdapterManager.addPeripheralAdapter(new CellProviderAdapter());
	}

}

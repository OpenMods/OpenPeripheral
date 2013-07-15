package openperipheral.core.integration;

import openperipheral.core.AdapterManager;
import openperipheral.core.TypeConversionRegistry;
import openperipheral.core.adapter.appeng.AdapterCellProvider;
import openperipheral.core.adapter.appeng.AdapterGridInterface;
import openperipheral.core.converter.ConverterIAEItemStack;
import openperipheral.core.converter.ConverterIItemList;

public class ModuleAppEng {

	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterCellProvider());
		AdapterManager.addPeripheralAdapter(new AdapterGridInterface());
		TypeConversionRegistry.registerTypeConverter(new ConverterIAEItemStack());
		TypeConversionRegistry.registerTypeConverter(new ConverterIItemList());
	}
	
}

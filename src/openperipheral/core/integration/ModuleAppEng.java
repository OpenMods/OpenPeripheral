package openperipheral.core.integration;

import openperipheral.core.AdapterManager;
import openperipheral.core.TypeConversionRegistry;
import openperipheral.core.adapter.appeng.AdapterCellProvider;
import openperipheral.core.adapter.appeng.AdapterGridTileEntity;
import openperipheral.core.adapter.appeng.AdapterTileController;
import openperipheral.core.converter.ConverterIItemList;

public class ModuleAppEng {

	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterCellProvider());
		AdapterManager.addPeripheralAdapter(new AdapterGridTileEntity());
		AdapterManager.addPeripheralAdapter(new AdapterTileController());
		TypeConversionRegistry.registerTypeConverter(new ConverterIItemList());
	}

}

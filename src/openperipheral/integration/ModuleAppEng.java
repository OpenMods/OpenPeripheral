package openperipheral.integration;

import openperipheral.AdapterManager;
import openperipheral.TypeConversionRegistry;
import openperipheral.adapter.appeng.AdapterCellProvider;
import openperipheral.adapter.appeng.AdapterGridTileEntity;
import openperipheral.adapter.appeng.AdapterTileController;
import openperipheral.converter.ConverterIItemList;

public class ModuleAppEng {

	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterCellProvider());
		AdapterManager.addPeripheralAdapter(new AdapterGridTileEntity());
		AdapterManager.addPeripheralAdapter(new AdapterTileController());
		TypeConversionRegistry.registerTypeConverter(new ConverterIItemList());
	}

}

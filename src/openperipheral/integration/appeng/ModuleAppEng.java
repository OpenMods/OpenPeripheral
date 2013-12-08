package openperipheral.integration.appeng;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import openmods.Mods;
import openperipheral.AdapterManager;
import openperipheral.TypeConversionRegistry;
import openperipheral.api.IIntegrationModule;
import openperipheral.converter.ConverterIItemList;

public class ModuleAppEng implements IIntegrationModule {

	@Override
	public String getModId() {
		return Mods.APPLIEDENERGISTICS;
	}

	@Override
	public void init() {
		AdapterManager.addPeripheralAdapter(new AdapterCellProvider());
		AdapterManager.addPeripheralAdapter(new AdapterGridTileEntity());
		AdapterManager.addPeripheralAdapter(new AdapterTileController());
		TypeConversionRegistry.registerTypeConverter(new ConverterIItemList());
	}

	@Override
	public void appendEntityInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {}

	@Override
	public void appendItemInfo(Map<String, Object> map, ItemStack itemstack) {}

}

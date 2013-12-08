package openperipheral.integration.forestry;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import openmods.Mods;
import openperipheral.AdapterManager;
import openperipheral.TypeConversionRegistry;
import openperipheral.api.IIntegrationModule;
import openperipheral.converter.ConverterIIndividual;
import forestry.api.genetics.AlleleManager;

public class ModuleForestry implements IIntegrationModule {
	@Override
	public String getModId() {
		return Mods.FORESTRY;
	}

	@Override
	public void init() {
		AdapterManager.addPeripheralAdapter(new AdapterBeeHousing());
		TypeConversionRegistry.registerTypeConverter(new ConverterIIndividual());
	}

	@Override
	public void appendEntityInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {
		// TODO: Add butterfly information
		// forestry.api.lepidopterology.IEntityButterfly
	}

	@Override
	public void appendItemInfo(Map<String, Object> map, ItemStack itemstack) {
		@SuppressWarnings("rawtypes")
		Map beeMap = (Map)TypeConversionRegistry.toLua(AlleleManager.alleleRegistry.getIndividual(itemstack));
		if (beeMap != null) {
			map.put("beeInfo", beeMap);
		}
	}
}

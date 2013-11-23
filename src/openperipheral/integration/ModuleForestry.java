package openperipheral.integration;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import openperipheral.AdapterManager;
import openperipheral.TypeConversionRegistry;
import openperipheral.adapter.forestry.AdapterBeeHousing;
import openperipheral.converter.ConverterIIndividual;
import forestry.api.genetics.AlleleManager;

public class ModuleForestry {
	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterBeeHousing());
		TypeConversionRegistry.registerTypeConverter(new ConverterIIndividual());
	}

	public static void entityToMap(Entity entity, HashMap map, Vec3 relativePos) {
		// TODO: Add butterfly information
		// forestry.api.lepidopterology.IEntityButterfly
	}

	@SuppressWarnings("rawtypes")
	public static void appendBeeInfo(HashMap<Object, Object> map, ItemStack itemstack) {
		Map beeMap = (Map)TypeConversionRegistry.toLua(AlleleManager.alleleRegistry.getIndividual(itemstack));
		if (beeMap != null) {
			map.put("beeInfo", beeMap);
		}
	}
}

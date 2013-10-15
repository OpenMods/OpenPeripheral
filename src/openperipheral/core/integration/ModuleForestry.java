package openperipheral.core.integration;

import java.util.HashMap;
import java.util.Map;

import forestry.api.genetics.AlleleManager;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import openperipheral.core.AdapterManager;
import openperipheral.core.TypeConversionRegistry;
import openperipheral.core.adapter.forestry.AdapterBeeHousing;
import openperipheral.core.converter.ConverterIIndividual;

public class ModuleForestry {
	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterBeeHousing());
		TypeConversionRegistry.registerTypeConverter(new ConverterIIndividual());
	}
	
	public static void entityToMap(Entity entity, HashMap map, Vec3 relativePos) {
		// TODO: Add butterfly information forestry.api.lepidopterology.IEntityButterfly
	}

	public static void appendBeeInfo(HashMap map, ItemStack itemstack) {
		Map beeMap = (Map) TypeConversionRegistry.toLua(AlleleManager.alleleRegistry.getIndividual(itemstack));
		if (beeMap != null) {
			map.put("beeInfo", beeMap);
		}
	}
}

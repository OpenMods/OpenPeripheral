package openperipheral.core.integration;

import java.util.Map;

import cofh.api.energy.IEnergyContainerItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.thermalexpansion.AdapterEnderAttuned;
import openperipheral.core.adapter.thermalexpansion.AdapterEnergyHandler;
import openperipheral.core.adapter.thermalexpansion.AdapterEnergyInfo;

public class ModuleThermalExpansion {
	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterEnergyHandler());
		AdapterManager.addPeripheralAdapter(new AdapterEnderAttuned());
		AdapterManager.addPeripheralAdapter(new AdapterEnergyInfo());
	}
	
	public static void appendRFEnergyInfo(Map map, ItemStack stack) {
		if (stack != null) {
			Item item = stack.getItem();		
			if (item instanceof IEnergyContainerItem) {
				IEnergyContainerItem energyItem = (IEnergyContainerItem)item;
				map.put("energyStored", energyItem.getEnergyStored(stack));
				map.put("maxEnergyStored", energyItem.getMaxEnergyStored(stack));
			}
		}
	}
}

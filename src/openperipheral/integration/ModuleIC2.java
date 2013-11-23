package openperipheral.integration;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openperipheral.AdapterManager;
import openperipheral.adapter.ic2.AdapterEnergyConductor;
import openperipheral.adapter.ic2.AdapterEnergySink;
import openperipheral.adapter.ic2.AdapterEnergySource;
import openperipheral.adapter.ic2.AdapterEnergyStorage;
import openperipheral.adapter.ic2.AdapterMassFab;
import openperipheral.adapter.ic2.AdapterReactor;
import openperipheral.adapter.ic2.AdapterReactorChamber;

public class ModuleIC2 {

	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterReactor());
		AdapterManager.addPeripheralAdapter(new AdapterReactorChamber());
		AdapterManager.addPeripheralAdapter(new AdapterMassFab());
		AdapterManager.addPeripheralAdapter(new AdapterEnergyConductor());
		AdapterManager.addPeripheralAdapter(new AdapterEnergySink());
		AdapterManager.addPeripheralAdapter(new AdapterEnergySource());
		AdapterManager.addPeripheralAdapter(new AdapterEnergyStorage());
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void appendIC2Info(Map map, ItemStack stack) {
		if (stack != null) {
			Item item = stack.getItem();
			if (item instanceof IElectricItem) {
				IElectricItem electricItem = (IElectricItem)item;
				HashMap<String,Object> electricInfo = new HashMap<String,Object>();

				electricInfo.put("tier", electricItem.getTier(stack));
				electricInfo.put("maxCharge", electricItem.getMaxCharge(stack));
				electricInfo.put("transferLimit", electricItem.getTransferLimit(stack));
				electricInfo.put("canProvideEnergy", electricItem.canProvideEnergy(stack));
				electricInfo.put("charge", ElectricItem.manager.getCharge(stack));

				map.put("electric", electricInfo);
			}
		}
	}
}

package openperipheral.integration;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import openmods.Mods;
import openperipheral.AdapterManager;
import openperipheral.adapter.ic2.*;
import openperipheral.api.IIntegrationModule;

public class ModuleIC2 implements IIntegrationModule {

	@Override
	public String getModId() {
		return Mods.IC2;
	}

	@Override
	public void init() {
		AdapterManager.addPeripheralAdapter(new AdapterReactor());
		AdapterManager.addPeripheralAdapter(new AdapterReactorChamber());
		AdapterManager.addPeripheralAdapter(new AdapterMassFab());
		AdapterManager.addPeripheralAdapter(new AdapterEnergyConductor());
		AdapterManager.addPeripheralAdapter(new AdapterEnergySink());
		AdapterManager.addPeripheralAdapter(new AdapterEnergySource());
		AdapterManager.addPeripheralAdapter(new AdapterEnergyStorage());
	}

	@Override
	public void appendItemInfo(Map<String, Object> map, ItemStack stack) {
		if (stack != null) {
			Item item = stack.getItem();
			if (item instanceof IElectricItem) {
				IElectricItem electricItem = (IElectricItem)item;
				HashMap<String, Object> electricInfo = new HashMap<String, Object>();

				electricInfo.put("tier", electricItem.getTier(stack));
				electricInfo.put("maxCharge", electricItem.getMaxCharge(stack));
				electricInfo.put("transferLimit", electricItem.getTransferLimit(stack));
				electricInfo.put("canProvideEnergy", electricItem.canProvideEnergy(stack));
				electricInfo.put("charge", ElectricItem.manager.getCharge(stack));

				map.put("electric", electricInfo);
			}
		}
	}

	@Override
	public void appendEntityInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {}
}

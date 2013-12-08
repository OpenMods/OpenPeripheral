package openperipheral.integration.thermalexpansion;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import openmods.Mods;
import openperipheral.AdapterManager;
import openperipheral.api.IIntegrationModule;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.item.IInventoryContainerItem;
import cofh.api.item.ISecureItem;

public class ModuleThermalExpansion implements IIntegrationModule {

	@Override
	public String getModId() {
		return Mods.THERMALEXPANSION;
	}

	@Override
	public void init() {
		AdapterManager.addPeripheralAdapter(new AdapterEnergyHandler());
		AdapterManager.addPeripheralAdapter(new AdapterEnderAttuned());
		AdapterManager.addPeripheralAdapter(new AdapterEnergyInfo());
		AdapterManager.addPeripheralAdapter(new AdapterTileLamp());
		AdapterManager.addPeripheralAdapter(new AdapterSecureTile());
	}

	@Override
	public void appendItemInfo(Map<String, Object> map, ItemStack stack) {
		if (stack != null) {
			Item item = stack.getItem();
			if (item instanceof IEnergyContainerItem) {
				IEnergyContainerItem energyItem = (IEnergyContainerItem)item;
				map.put("energyStored", energyItem.getEnergyStored(stack));
				map.put("maxEnergyStored", energyItem.getMaxEnergyStored(stack));
			}
			if (item instanceof IInventoryContainerItem) {
				IInventoryContainerItem itemItem = (IInventoryContainerItem)item;
				map.put("sizeInventory", itemItem.getSizeInventory(stack));
				map.put("inventoryContents", itemItem.getInventoryContents(stack));
			}
			if (item instanceof ISecureItem) {
				ISecureItem secureItem = (ISecureItem)item;
				map.put("owner", secureItem.getOwnerString());
			}
		}
	}

	@Override
	public void appendEntityInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {}

}

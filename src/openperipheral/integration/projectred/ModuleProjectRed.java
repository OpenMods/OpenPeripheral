package openperipheral.integration.projectred;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import openmods.Mods;
import openperipheral.AdapterManager;
import openperipheral.api.IIntegrationModule;

public class ModuleProjectRed implements IIntegrationModule {

	@Override
	public String getModId() {
		return Mods.PROJECTRED_TRANSMISSION;
	}

	@Override
	public void init() {
		AdapterManager.addPeripheralAdapter(new AdapterBundledCablePart());
		AdapterManager.addPeripheralAdapter(new AdapterInsulatedRedwirePart());
	}

	@Override
	public void appendEntityInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {}

	@Override
	public void appendItemInfo(Map<String, Object> map, ItemStack itemstack) {}

}

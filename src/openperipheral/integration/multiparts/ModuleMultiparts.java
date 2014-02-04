package openperipheral.integration.multiparts;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import openmods.Mods;
import openperipheral.adapter.AdapterManager;
import openperipheral.api.IIntegrationModule;

public class ModuleMultiparts implements IIntegrationModule {

	@Override
	public String getModId() {
		return Mods.MULTIPART;
	}

	@Override
	public void init() {
		AdapterManager.addPeripheralAdapter(new AdapterMultipart());
	}

	@Override
	public void appendItemInfo(Map<String, Object> map, ItemStack stack) {}

	@Override
	public void appendEntityInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {}
}

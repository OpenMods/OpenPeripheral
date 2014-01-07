package openperipheral.integration.tconstruct;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import openmods.Mods;
import openperipheral.adapter.AdapterManager;
import openperipheral.api.IIntegrationModule;

public class ModuleTConstruct implements IIntegrationModule {
	@Override
	public void init() {
		//AdapterManager.addPeripheralAdapter(new AdapterDrawbridgeLogicBase());
	}

	@Override
	public String getModId() {
		return Mods.TINKERSCONSTRUCT;
	}

	@Override
	public void appendEntityInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {}

	@Override
	public void appendItemInfo(Map<String, Object> map, ItemStack itemstack) {}

}

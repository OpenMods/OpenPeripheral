package openperipheral.core.integration;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.AdapterTicketMachine;

public class ModuleRailcraft {

	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterTicketMachine());
	}

	public static void entityToMap(Entity entity, HashMap map, Vec3 relativePos) {
		// TODO: Add railcraft support
	}
}

package openperipheral.core.integration;

import java.util.HashMap;

import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.thaumcraft.AdapterAspectContainer;
import openperipheral.core.adapter.thaumcraft.AdapterNode;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class ModuleThaumcraft {
	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterAspectContainer());
		AdapterManager.addPeripheralAdapter(new AdapterNode());
	}
	
	public static void entityToMap(Entity entity, HashMap map, Vec3 relativePos) {
		
	}
}

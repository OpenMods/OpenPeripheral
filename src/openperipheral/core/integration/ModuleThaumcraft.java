package openperipheral.core.integration;

import java.util.HashMap;

import openperipheral.api.BlacklistRegistry;
import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.thaumcraft.AdapterAspectContainer;
import openperipheral.core.adapter.thaumcraft.AdapterNode;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class ModuleThaumcraft {
	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterAspectContainer());
		AdapterManager.addPeripheralAdapter(new AdapterNode());
		
		// These have nothing to interact with, but were registering, removing them
		BlacklistRegistry.registerClass("thaumcraft.common.tiles.TileArcaneBoreBase");
		BlacklistRegistry.registerClass("thaumcraft.common.tiles.TileBellows");
		BlacklistRegistry.registerClass("thaumcraft.common.tiles.TileTable");
	}
	
	public static void entityToMap(Entity entity, HashMap map, Vec3 relativePos) {
		
	}
}

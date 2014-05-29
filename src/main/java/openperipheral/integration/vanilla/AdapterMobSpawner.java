package openperipheral.integration.vanilla;

import net.minecraft.tileentity.TileEntityMobSpawner;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterMobSpawner implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityMobSpawner.class;
	}

	@LuaMethod(returnType = LuaType.STRING, description = "The name of the mob that spawns from the spawner")
	public String getSpawningMobName(TileEntityMobSpawner spawner) {
		return spawner.func_145881_a().getEntityNameToSpawn();
	}
}
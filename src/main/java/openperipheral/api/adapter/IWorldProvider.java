package openperipheral.api.adapter;

import net.minecraft.world.World;

/**
 * This interface is no longer used by OpenPeripheralCore, but is still used by some child mods (like OpenPeriperal-Integration)
 */
public interface IWorldProvider {
	public World getWorld();

	public boolean isValid();
}

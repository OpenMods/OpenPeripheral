package openperipheral.api.adapter;

import net.minecraft.util.BlockPos;

/**
 * This interface is no longer used by OpenPeripheralCore, but is still used by some child mods (like OpenPeriperal-Integration)
 */
public interface IWorldPosProvider extends IWorldProvider {
	public BlockPos getPos();
}

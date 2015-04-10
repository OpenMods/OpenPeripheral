package openperipheral.api.architecture.cc;

import openperipheral.api.IApiInterface;
import openperipheral.api.adapter.GenerationFailedException;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.peripheral.IPeripheral;

/**
 * API interface for wrapping Java objects to ComputerCraft structures.
 * It will return same objects as normally returned to ComputerCraft for TileEntities and converted objects.
 */
public interface IComputerCraftObjectsFactory extends IApiInterface {

	/**
	 * @throws GenerationFailedException
	 */
	public ILuaObject wrapObject(Object target);

	/**
	 * @throws GenerationFailedException
	 */
	public IPeripheral createPeripheral(Object target);
}

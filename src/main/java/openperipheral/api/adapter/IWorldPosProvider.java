package openperipheral.api.adapter;

/**
 * This interface is no longer used by OpenPeripheralCore, but is still used by some child mods (like OpenPeriperal-Integration)
 */
public interface IWorldPosProvider extends IWorldProvider {
	public int getX();

	public int getY();

	public int getZ();
}

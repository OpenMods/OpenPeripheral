package openperipheral.api;

/**
 * If wrapped object implements this interface, OpenPeripheral will pass HostedPeripheral update calls to it
 */
public interface IUpdateHandler {
	public void onPeripheralUpdate();
}

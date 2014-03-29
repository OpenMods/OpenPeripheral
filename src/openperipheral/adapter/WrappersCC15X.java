package openperipheral.adapter;

public class WrappersCC15X {
	public static dan200.computer.api.ILuaObject createObjectWrapper(Object obj) {
		return AdapterManager.wrapObject(obj);
	}

	public static dan200.computer.api.IHostedPeripheral createHostedPeripheral(Object target) {
		return PeripheralHandlers.createHostedPeripheral(target);
	}
}

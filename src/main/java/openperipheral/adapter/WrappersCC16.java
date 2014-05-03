package openperipheral.adapter;

public class WrappersCC16 {
	public static dan200.computercraft.api.lua.ILuaObject wrapObject(Object obj) {
		return AdapterManager.wrapObject(obj);
	}

	public static dan200.computercraft.api.peripheral.IPeripheral createPeripheral(Object target) {
		return PeripheralHandlers.createAdaptedPeripheral(target);
	}
}

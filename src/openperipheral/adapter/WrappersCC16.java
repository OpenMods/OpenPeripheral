package openperipheral.adapter;

public class WrappersCC16 {
	private static UnsupportedOperationException newAPIException() {
		return new UnsupportedOperationException("Invalid ComputerCraft API used. This version is not compatible with mods using ComputerCraft 1.6 and newer");
	}

	public static dan200.computercraft.api.lua.ILuaObject wrapObject(Object obj) {
		throw newAPIException();
	}

	public static dan200.computercraft.api.peripheral.IPeripheral createPeripheral(Object target) {
		throw newAPIException();
	}
}

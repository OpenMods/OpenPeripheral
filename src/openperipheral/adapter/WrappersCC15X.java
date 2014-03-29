package openperipheral.adapter;

public class WrappersCC15X {
	private static UnsupportedOperationException oldAPIException() {
		return new UnsupportedOperationException("Invalid ComputerCraft API used. This version is not compatible with mods using ComputerCraft 1.5X");
	}

	public static dan200.computer.api.ILuaObject createObjectWrapper(Object obj) {
		throw oldAPIException();
	}

	public static dan200.computercraft.api.peripheral.IPeripheral createHostedPeripheral(Object target) {
		throw oldAPIException();
	}
}

package openperipheral.interfaces.cc.providers;

import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.peripheral.IPeripheral;
import openperipheral.api.architecture.cc.IComputerCraftObjectsFactory;
import openperipheral.interfaces.cc.wrappers.LuaObjectWrapper;

public class AdapterFactoryWrapperCC implements IComputerCraftObjectsFactory {

	@Override
	public ILuaObject wrapObject(Object target) {
		return LuaObjectWrapper.wrap(target);
	}

	@Override
	public IPeripheral createPeripheral(Object target) {
		return PeripheralProvider.createAdaptedPeripheralWrapped(target);
	}

}

package openperipheral.interfaces.cc.providers;

import openperipheral.ApiImplementation;
import openperipheral.api.architecture.cc.IComputerCraftObjectsFactory;
import openperipheral.interfaces.cc.wrappers.LuaObjectWrapper;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.peripheral.IPeripheral;

@ApiImplementation
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

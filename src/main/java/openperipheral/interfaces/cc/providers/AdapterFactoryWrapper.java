package openperipheral.interfaces.cc.providers;

import openperipheral.ApiImplementation;
import openperipheral.api.IAdapterFactory;
import openperipheral.interfaces.cc.wrappers.LuaObjectWrapper;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.peripheral.IPeripheral;

@ApiImplementation
public class AdapterFactoryWrapper implements IAdapterFactory {

	@Override
	public ILuaObject wrapObject(Object target) {
		return LuaObjectWrapper.wrap(target);
	}

	@Override
	public IPeripheral createPeripheral(Object target) {
		return PeripheralProvider.createAdaptedPeripheralSafe(target);
	}

}

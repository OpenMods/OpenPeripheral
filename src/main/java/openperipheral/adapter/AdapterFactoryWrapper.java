package openperipheral.adapter;

import openperipheral.ApiImplementation;
import openperipheral.api.IAdapterFactory;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.peripheral.IPeripheral;

@ApiImplementation
public class AdapterFactoryWrapper implements IAdapterFactory {

	@Override
	public ILuaObject wrapObject(Object target) {
		return AdapterManager.wrapObject(target);
	}

	@Override
	public IPeripheral createPeripheral(Object target) {
		return PeripheralHandlers.createAdaptedPeripheralSafe(target);
	}

}

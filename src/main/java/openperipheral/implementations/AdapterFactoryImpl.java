package openperipheral.implementations;

import openperipheral.adapter.AdapterManager;
import openperipheral.adapter.PeripheralHandlers;
import openperipheral.api.IAdapterFactory;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.peripheral.IPeripheral;

@ApiImplementation
public class AdapterFactoryImpl implements IAdapterFactory {

	@Override
	public ILuaObject wrapObject(Object target) {
		return AdapterManager.wrapObject(target);
	}

	@Override
	public IPeripheral createPeripheral(Object target) {
		return PeripheralHandlers.createAdaptedPeripheralSafe(target);
	}

}

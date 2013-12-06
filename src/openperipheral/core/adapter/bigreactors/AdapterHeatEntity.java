package openperipheral.core.adapter.bigreactors;

import dan200.computer.api.IComputerAccess;
import erogenousbeef.bigreactors.api.IHeatEntity;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterHeatEntity implements IPeripheralAdapter{

	@Override
	public Class<?> getTargetClass() {
		return IHeatEntity.class;
	}
	
	@LuaMethod(description="returns the amount of heat in the entity, in celsius.", returnType=LuaType.NUMBER)
	public float getHeat(IComputerAccess computer, IHeatEntity entity) {
		return entity.getHeat();
	}
	
	@LuaMethod(description="return Thermal conductivity constant, the percentage heat difference to absorb in 1 sec", returnType=LuaType.NUMBER)
	public float getThermalConductivity(IComputerAccess computer, IHeatEntity entity) {
		return entity.getThermalConductivity();
	}
}

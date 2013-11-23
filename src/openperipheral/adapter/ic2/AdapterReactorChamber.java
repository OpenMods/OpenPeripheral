package openperipheral.adapter.ic2;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

public class AdapterReactorChamber implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IReactorChamber.class;
	}

	@LuaMethod(onTick = false, description = "Get the heat of the reactor", returnType = LuaType.NUMBER)
	public int getHeat(IComputerAccess computer, IReactorChamber chamber) {
		IReactor reactor = chamber.getReactor();
		if (reactor == null) { return 0; }
		return reactor.getHeat();
	}

	@LuaMethod(onTick = false, description = "Get the maximum heat of the reactor before it explodes", returnType = LuaType.NUMBER)
	public int getMaxHeat(IComputerAccess computer, IReactorChamber chamber) {
		IReactor reactor = chamber.getReactor();
		if (reactor == null) { return 0; }
		return reactor.getMaxHeat();
	}

	@LuaMethod(onTick = false, description = "Get the EU output of this reactor", returnType = LuaType.NUMBER)
	public float getEUOutput(IComputerAccess computer, IReactorChamber chamber) {
		IReactor reactor = chamber.getReactor();
		if (reactor == null) { return 0; }
		return reactor.getReactorEnergyOutput();
	}

	@LuaMethod(onTick = false, description = "Returns true if the reactor is active", returnType = LuaType.BOOLEAN)
	public boolean isActive(IComputerAccess computer, IReactorChamber chamber) {
		IReactor reactor = chamber.getReactor();
		if (reactor == null) { return false; }
		return reactor.produceEnergy();
	}
}

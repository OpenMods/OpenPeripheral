package openperipheral.integration.ic2;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

import com.google.common.base.Preconditions;

public class AdapterReactorChamber implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IReactorChamber.class;
	}

	private static IReactor getReactor(IReactorChamber chamber) {
		IReactor reactor = chamber.getReactor();
		Preconditions.checkNotNull(reactor, "No reactor");
		return reactor;
	}

	@LuaMethod(onTick = false, description = "Check if reactor is in valid state", returnType = LuaType.BOOLEAN)
	public boolean isValid(IReactorChamber chamber) {
		return chamber.getReactor() != null;
	}

	@LuaMethod(onTick = false, description = "Get the heat of the reactor", returnType = LuaType.NUMBER)
	public int getHeat(IReactorChamber chamber) {
		return getReactor(chamber).getHeat();
	}

	@LuaMethod(onTick = false, description = "Get the maximum heat of the reactor before it explodes", returnType = LuaType.NUMBER)
	public int getMaxHeat(IReactorChamber chamber) {
		return getReactor(chamber).getMaxHeat();
	}

	@LuaMethod(onTick = false, description = "Get the EU output of this reactor", returnType = LuaType.NUMBER)
	public float getEUOutput(IReactorChamber chamber) {
		return getReactor(chamber).getReactorEnergyOutput();
	}

	@LuaMethod(onTick = false, description = "Returns true if the reactor is active", returnType = LuaType.BOOLEAN)
	public boolean isActive(IReactorChamber chamber) {
		return getReactor(chamber).produceEnergy();
	}
}

package openperipheral.core.adapter.ic2;

import ic2.api.reactor.IReactor;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

public class AdapterReactor implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return IReactor.class;
	}

	@LuaMethod(onTick = false, description = "Get the heat of the reactor", returnType = LuaType.NUMBER)
	public int getHeat(IComputerAccess computer, IReactor reactor) {
		return reactor.getHeat();
	}

	@LuaMethod(onTick = false, description = "Get the maximum heat of the reactor before it explodes", returnType = LuaType.NUMBER)
	public int getMaxHeat(IComputerAccess computer, IReactor reactor) {
		return reactor.getMaxHeat();
	}

	@LuaMethod(onTick = false, description = "Get the EU output of this reactor", returnType = LuaType.NUMBER)
	public float getEUOutput(IComputerAccess computer, IReactor reactor) {
		return reactor.getReactorEnergyOutput();
	}

	@LuaMethod(onTick = false, description = "Returns true if the reactor is active", returnType = LuaType.BOOLEAN)
	public boolean isActive(IComputerAccess computer, IReactor reactor) {
		return reactor.produceEnergy();
	}
}

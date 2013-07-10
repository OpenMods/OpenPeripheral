package openperipheral.core.adapter.ic2;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import dan200.computer.api.IComputerAccess;

public class AdapterReactorChamber implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return IReactorChamber.class;
	}

	@LuaMethod
	public int getHeat(IComputerAccess computer, IReactorChamber chamber) {
		IReactor reactor = chamber.getReactor();
		if (reactor == null) {
			return 0;
		}
		return reactor.getHeat();
	}

	@LuaMethod
	public int getMaxHeat(IComputerAccess computer, IReactorChamber chamber) {
		IReactor reactor = chamber.getReactor();
		if (reactor == null) {
			return 0;
		}
		return reactor.getMaxHeat();
	}

	@LuaMethod
	public int getEUOutput(IComputerAccess computer, IReactorChamber chamber) {
		IReactor reactor = chamber.getReactor();
		if (reactor == null) {
			return 0;
		}
		return reactor.getOutput();
	}

	@LuaMethod
	public boolean isActive(IComputerAccess computer, IReactorChamber chamber) {
		IReactor reactor = chamber.getReactor();
		if (reactor == null) {
			return false;
		}
		return reactor.produceEnergy();
	}
}

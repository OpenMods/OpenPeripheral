package openperipheral.core.adapter.ic2;

import ic2.api.reactor.IReactor;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import dan200.computer.api.IComputerAccess;

public class AdapterReactor implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return IReactor.class;
	}

	@LuaMethod
	public int getHeat(IComputerAccess computer, IReactor reactor) {
		return reactor.getHeat();
	}

	@LuaMethod
	public int getMaxHeat(IComputerAccess computer, IReactor reactor) {
		return reactor.getMaxHeat();
	}

	@LuaMethod
	public int getEUOutput(IComputerAccess computer, IReactor reactor) {
		return reactor.getOutput();
	}

	@LuaMethod
	public boolean isActive(IComputerAccess computer, IReactor reactor) {
		return reactor.produceEnergy();
	}
}

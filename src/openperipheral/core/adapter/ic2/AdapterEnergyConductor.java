package openperipheral.core.adapter.ic2;

import ic2.api.energy.tile.IEnergyConductor;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import dan200.computer.api.IComputerAccess;

public class AdapterEnergyConductor implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return IEnergyConductor.class;
	}

	@LuaMethod
	public double getEUConductionLoss(IComputerAccess computer,
			IEnergyConductor conductor) {
		return conductor.getConductionLoss();
	}

	@LuaMethod
	public double getEUConductorBreakdownEnergy(IComputerAccess computer,
			IEnergyConductor conductor) {
		return conductor.getConductorBreakdownEnergy();
	}

	@LuaMethod
	public double getEUInsulationBreakdownEnergy(IComputerAccess computer,
			IEnergyConductor conductor) {
		return conductor.getInsulationBreakdownEnergy();
	}

	@LuaMethod
	public double getEUInsulationEnergyAbsorption(IComputerAccess computer,
			IEnergyConductor conductor) {
		return conductor.getInsulationEnergyAbsorption();
	}

}

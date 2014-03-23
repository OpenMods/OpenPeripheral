package openperipheral.integration.ic2;

import ic2.api.energy.tile.IEnergyConductor;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterEnergyConductor implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IEnergyConductor.class;
	}

	@LuaMethod(onTick = false, description = "Get the EU conduction loss", returnType = LuaType.NUMBER)
	public double getEUConductionLoss(IEnergyConductor conductor) {
		return conductor.getConductionLoss();
	}

	@LuaMethod(onTick = false, description = "Get the EU conductor breakdown energy", returnType = LuaType.NUMBER)
	public double getEUConductorBreakdownEnergy(IEnergyConductor conductor) {
		return conductor.getConductorBreakdownEnergy();
	}

	@LuaMethod(onTick = false, description = "Get the EU insulation breakdown energy", returnType = LuaType.NUMBER)
	public double getEUInsulationBreakdownEnergy(IEnergyConductor conductor) {
		return conductor.getInsulationBreakdownEnergy();
	}

	@LuaMethod(onTick = false, description = "Get the EU insulation energy absorption", returnType = LuaType.NUMBER)
	public double getEUInsulationEnergyAbsorption(IEnergyConductor conductor) {
		return conductor.getInsulationEnergyAbsorption();
	}

}

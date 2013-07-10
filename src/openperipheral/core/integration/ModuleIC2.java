package openperipheral.core.integration;

import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.ic2.AdapterEnergyConductor;
import openperipheral.core.adapter.ic2.AdapterEnergySink;
import openperipheral.core.adapter.ic2.AdapterEnergySource;
import openperipheral.core.adapter.ic2.AdapterEnergyStorage;
import openperipheral.core.adapter.ic2.AdapterMassFab;
import openperipheral.core.adapter.ic2.AdapterReactor;
import openperipheral.core.adapter.ic2.AdapterReactorChamber;

public class ModuleIC2 {
	
	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterReactor());
		AdapterManager.addPeripheralAdapter(new AdapterReactorChamber());
		AdapterManager.addPeripheralAdapter(new AdapterMassFab());
		AdapterManager.addPeripheralAdapter(new AdapterEnergyConductor());
		AdapterManager.addPeripheralAdapter(new AdapterEnergySink());
		AdapterManager.addPeripheralAdapter(new AdapterEnergySource());
		AdapterManager.addPeripheralAdapter(new AdapterEnergyStorage());
	}

}

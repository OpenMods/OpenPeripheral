package openperipheral.interfaces.oc;

import openmods.Mods;
import openperipheral.ArchitectureChecker.IArchitecturePredicate;
import openperipheral.Config;
import cpw.mods.fml.common.Loader;

public class OpenComputersChecker implements IArchitecturePredicate {

	@Override
	public boolean isEnabled() {
		return Config.interfaceOpenComputers
				&& Loader.isModLoaded(Mods.OPENCOMPUTERS);
	}

}

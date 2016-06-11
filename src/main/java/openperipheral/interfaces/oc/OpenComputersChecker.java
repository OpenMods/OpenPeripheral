package openperipheral.interfaces.oc;

import cpw.mods.fml.common.Loader;
import openmods.Mods;
import openperipheral.ArchitectureChecker.IArchitecturePredicate;
import openperipheral.Config;

public class OpenComputersChecker implements IArchitecturePredicate {

	@Override
	public boolean isEnabled() {
		return Config.interfaceOpenComputers
				&& Loader.isModLoaded(Mods.OPENCOMPUTERS);
	}

}

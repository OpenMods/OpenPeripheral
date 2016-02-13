package openperipheral.interfaces.cc;

import openmods.Mods;
import openperipheral.ArchitectureChecker.IArchitecturePredicate;
import openperipheral.Config;
import cpw.mods.fml.common.Loader;

public class ComputerCraftChecker implements IArchitecturePredicate {

	@Override
	public boolean isEnabled() {
		return Config.interfaceComputerCraft
				&& Loader.isModLoaded(Mods.COMPUTERCRAFT);
	}

}

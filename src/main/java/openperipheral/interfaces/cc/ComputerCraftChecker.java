package openperipheral.interfaces.cc;

import net.minecraftforge.fml.common.Loader;
import openmods.Mods;
import openperipheral.ArchitectureChecker.IArchitecturePredicate;
import openperipheral.Config;

public class ComputerCraftChecker implements IArchitecturePredicate {

	@Override
	public boolean isEnabled() {
		return Config.interfaceComputerCraft
				&& Loader.isModLoaded(Mods.COMPUTERCRAFT);
	}

}

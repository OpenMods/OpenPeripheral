package openperipheral.core.integration.mps;

import net.machinemuse.api.ModuleManager;

public class MPSModule {

	public static void init() {
		ModuleManager.addModule(new GlassesModule());
	}

}

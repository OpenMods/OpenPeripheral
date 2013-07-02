package openperipheral.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RobotUpgradeManager {
	
	private static List<IRobotUpgradeProvider> providers = new ArrayList<IRobotUpgradeProvider>();
	
	public static List<IRobotUpgradeProvider> getProviders() {
		return providers;
	}
	
	public static void registerUpgradeProvider(IRobotUpgradeProvider upgrade) {
		providers.add(upgrade);
	}
	
	public static IRobotUpgradeProvider getSupplierById(String id) {
		for (IRobotUpgradeProvider provider : providers) {
			if (provider.getUpgradeId().equals(id)) {
				return provider;
			}
		}
		return null;
	}
	
}

package openperipheral.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RobotUpgradeManager {
	
	private static List<IRobotUpgradeDefinition> suppliers = new ArrayList<IRobotUpgradeDefinition>();
	
	public static List<IRobotUpgradeDefinition> getSuppliers() {
		return suppliers;
	}
	
	public static void registerUpgradeSupplier(IRobotUpgradeDefinition upgrade) {
		suppliers.add(upgrade);
	}
	
	public static IRobotUpgradeDefinition getSupplierById(String id) {
		for (IRobotUpgradeDefinition supplier : suppliers) {
			if (supplier.getUpgradeId().equals(id)) {
				return supplier;
			}
		}
		return null;
	}
	
}

package openperipheral.common.integration.forestry;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRestriction;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import forestry.api.apiculture.IBeeHousing;

public class GetSpecificBeeMethod implements IPeripheralMethodDefinition {

	private String methodName;
	
	public GetSpecificBeeMethod(String methodName) {
		this.methodName = methodName;
	}
	
	@Override
	public HashMap<Integer, String> getReplacements() {
		return null;
	}

	@Override
	public String getPostScript() {
		return null;
	}

	@Override
	public boolean getCauseTileUpdate() {
		return false;
	}

	@Override
	public Class[] getRequiredParameters() {
		return null;
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public String getLuaName() {
		return methodName;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean needsSanitize() {
		return false;
	}

	@Override
	public ArrayList<IRestriction> getRestrictions(int index) {
		return null;
	}

	@Override
	public Object execute(Object target, Object[] args) throws Exception {
		if (target instanceof IBeeHousing) {
			IBeeHousing housing = (IBeeHousing) target;
			ItemStack bee = null;
			if (methodName.equals("getQueen")) {
				bee = housing.getQueen();
			}else {
				bee = housing.getDrone();
			}
			if (bee != null) {
				return BeeUtils.beeToMap(bee);
			}
		}
		return null;
	}

}

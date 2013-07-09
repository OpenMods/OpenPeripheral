package openperipheral.core.integration.forestry;

import java.util.ArrayList;
import java.util.HashMap;

import dan200.computer.api.IComputerAccess;

import forestry.api.apiculture.IBeeHousing;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.util.ReflectionHelper;

public class BeeHousingAdapter implements IPeripheralAdapter {

	private Class klazz = null;
	
	public BeeHousingAdapter() {
		klazz = ReflectionHelper.getClass("forestry.api.apiculture.IBeeHousing");
	}
	
	@Override
	public Class getTargetClass() {
		return klazz;
	}

	@LuaMethod
	public HashMap getQueen(IComputerAccess computer, IBeeHousing target) {
		IBeeHousing housing = (IBeeHousing) target;
		ItemStack bee = housing.getQueen();
		if (bee != null) {
			return BeeUtils.beeToMap(bee);
		}
		return null;
	}

	@LuaMethod
	public HashMap getDrone(IComputerAccess computer, IBeeHousing target) {
		IBeeHousing housing = (IBeeHousing) target;
		ItemStack bee = housing.getDrone();
		if (bee != null) {
			return BeeUtils.beeToMap(bee);
		}
		return null;
	}
}

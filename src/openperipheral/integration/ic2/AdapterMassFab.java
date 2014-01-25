package openperipheral.integration.ic2;

import openmods.utils.ReflectionHelper;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

public class AdapterMassFab implements IPeripheralAdapter {
	private static final Class<?> CLAZZ = ReflectionHelper.getClass("ic2.core.block.machine.tileentity.TileEntityMatter");

	@Override
	public Class<?> getTargetClass() {
		return CLAZZ;
	}

	@LuaMethod(onTick = false, description = "Get the current progress as a percentage", returnType = LuaType.NUMBER)
	public double getProgress(IComputerAccess computer, Object massfab) {
		double energy = (Double)ReflectionHelper.getProperty(CLAZZ, massfab, "energy");
		return Math.min(energy / 10000, 100);
	}

}

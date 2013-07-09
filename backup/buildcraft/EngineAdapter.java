package openperipheral.core.integration.buildcraft;

import java.util.ArrayList;

import dan200.computer.api.IComputerAccess;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.util.ReflectionHelper;

public class EngineAdapter implements IPeripheralAdapter {

	private Class klazz = null;

	public EngineAdapter() {
		klazz = ReflectionHelper.getClass("buildcraft.energy.TileEngine");
	}
	
	@Override
	public Class getTargetClass() {
		return klazz;
	}

	@LuaMethod
	public double getCurrentOutput(IComputerAccess computer, Object tile) throws Exception {
		Object engine = ReflectionHelper.callMethod(false, "", tile, new String[] { "getEngine" });
		if (engine != null) {
			return (Double) ReflectionHelper.callMethod(false, "", engine, new String[] { "getCurrentOutput" });
		}
		return 0;
	}
}

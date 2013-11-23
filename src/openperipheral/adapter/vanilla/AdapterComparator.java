package openperipheral.adapter.vanilla;

import net.minecraft.tileentity.TileEntityComparator;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

public class AdapterComparator implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityComparator.class;
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the strength of the output signal")
	public int getOutputSignal(IComputerAccess computer, TileEntityComparator comparator) {
		return comparator.getOutputSignal();
	}

}

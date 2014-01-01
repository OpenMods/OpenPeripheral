package openperipheral.core.adapter.tconstruct;

import dan200.computer.api.IComputerAccess;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import tconstruct.library.blocks.IDrawbridgeLogicBase;

public class AdapterDrawbridgeLogicBase implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return IDrawbridgeLogicBase.class;
	}
	
	@LuaMethod(description="Checks if the drawbridge is extended or not", returnType=LuaType.BOOLEAN)
	public boolean hasExtended(IComputerAccess computer, IDrawbridgeLogicBase drawbridge) {
		return drawbridge.hasExtended();
	}

}
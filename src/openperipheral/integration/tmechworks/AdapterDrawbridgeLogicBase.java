package openperipheral.integration.tmechworks;

import openperipheral.api.*;
import tmechworks.lib.blocks.IDrawbridgeLogicBase;
import dan200.computer.api.IComputerAccess;

@OnTickSafe
public class AdapterDrawbridgeLogicBase implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IDrawbridgeLogicBase.class;
	}

	@LuaMethod(description = "Checks if the drawbridge is extended or not", returnType = LuaType.BOOLEAN)
	public boolean hasExtended(IComputerAccess computer, IDrawbridgeLogicBase drawbridge) {
		return drawbridge.hasExtended();
	}

}

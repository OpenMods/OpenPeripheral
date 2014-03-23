package openperipheral.integration.tmechworks;

import openperipheral.api.*;
import tmechworks.lib.blocks.IDrawbridgeLogicBase;

@OnTickSafe
public class AdapterDrawbridgeLogicBase implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IDrawbridgeLogicBase.class;
	}

	@LuaMethod(description = "Checks if the drawbridge is extended or not", returnType = LuaType.BOOLEAN)
	public boolean hasExtended(IDrawbridgeLogicBase drawbridge) {
		return drawbridge.hasExtended();
	}

}

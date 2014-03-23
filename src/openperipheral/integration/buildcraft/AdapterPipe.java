package openperipheral.integration.buildcraft;

import openperipheral.api.*;
import buildcraft.api.transport.IPipe;

public class AdapterPipe implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IPipe.class;
	}

	@LuaMethod(description = "Checks if this pipe has a gate.", returnType = LuaType.BOOLEAN)
	public boolean hasGate(IPipe pipe) {
		return pipe.hasGate();
	}

	@LuaMethod(description = "Checks if a wire of your colour choice is on the pipe.", returnType = LuaType.BOOLEAN, args = {
			@Arg(name = "color", description = "The colour of the wire, can be \"Yellow\", \"Green\", \"Blue\" and \"Red\". (Case sensitive)", type = LuaType.STRING)
	})
	public boolean isWired(IPipe pipe, String colour) {
		try {
			IPipe.WireColor color = IPipe.WireColor.valueOf(colour);
			return pipe.isWired(color);
		} catch (IllegalArgumentException e) {}
		return false;
	}

}

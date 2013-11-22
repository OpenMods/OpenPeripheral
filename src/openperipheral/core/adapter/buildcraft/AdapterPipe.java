package openperipheral.core.adapter.buildcraft;

import dan200.computer.api.IComputerAccess;
import buildcraft.api.transport.IPipe;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.api.Arg;

public class AdapterPipe implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IPipe.class;
	}

	@LuaMethod(description = "Checks if this pipe has a gate.", returnType = LuaType.BOOLEAN)
	public boolean hasGate(IComputerAccess computer, IPipe pipe) {
		return pipe.hasGate();
	}
	@LuaMethod(description = "Checks if a wire of your colour choice is on the pipe.", returnType = LuaType.BOOLEAN, args={
			@Arg(name="color", description="The colour of the wire, can be \"Yellow\", \"Green\", \"Blue\" and \"Red\". (Case sensitive)", type=LuaType.STRING)
	})
	public boolean isWired(IComputerAccess computer, IPipe pipe, String colour) {
		try {
			IPipe.WireColor color = IPipe.WireColor.valueOf(colour);
			return pipe.isWired(color);
		} catch (IllegalArgumentException e){}
		return false;
	}

}

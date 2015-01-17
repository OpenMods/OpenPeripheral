package openperipheral.interfaces.cc;

import openperipheral.adapter.*;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class Registries {

	public static final MethodsValidator PERIPHERAL_VALIDATOR = new MethodsValidator(AdapterRegistry.PERIPHERAL_ADAPTERS);

	static {
		PERIPHERAL_VALIDATOR.addArg(DefaultArgNames.ARG_COMPUTER, IComputerAccess.class);
		PERIPHERAL_VALIDATOR.addArg(DefaultArgNames.ARG_CONTEXT, ILuaContext.class);
	}

	public static final ComposedMethodsFactory PERIPHERAL_METHODS_FACTORY = new ComposedMethodsFactory(AdapterRegistry.PERIPHERAL_ADAPTERS);

	public static final MethodsValidator OBJECT_VALIDATOR = new MethodsValidator(AdapterRegistry.OBJECT_ADAPTERS);

	static {
		OBJECT_VALIDATOR.addArg(DefaultArgNames.ARG_COMPUTER, IComputerAccess.class);
		OBJECT_VALIDATOR.addArg(DefaultArgNames.ARG_CONTEXT, ILuaContext.class);
	}

	public static final ComposedMethodsFactory OBJECT_METHODS_FACTORY = new ComposedMethodsFactory(AdapterRegistry.OBJECT_ADAPTERS);

}

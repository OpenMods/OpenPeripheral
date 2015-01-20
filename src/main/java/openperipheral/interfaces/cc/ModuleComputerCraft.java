package openperipheral.interfaces.cc;

import openperipheral.TypeConvertersProvider;
import openperipheral.adapter.*;
import openperipheral.adapter.method.LuaTypeQualifier;
import openperipheral.api.Architectures;
import openperipheral.api.ITypeConvertersRegistry;
import openperipheral.api.LuaArgType;
import openperipheral.interfaces.cc.providers.PeripheralProvider;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class ModuleComputerCraft {

	public static final MethodsValidator PERIPHERAL_VALIDATOR = new MethodsValidator(AdapterRegistry.PERIPHERAL_ADAPTERS);

	static {
		PERIPHERAL_VALIDATOR.addArg(DefaultEnvArgs.ARG_COMPUTER, IComputerAccess.class);
		PERIPHERAL_VALIDATOR.addArg(DefaultEnvArgs.ARG_CONTEXT, ILuaContext.class);
	}

	public static final ComposedMethodsFactory PERIPHERAL_METHODS_FACTORY = new ComposedMethodsFactory(AdapterRegistry.PERIPHERAL_ADAPTERS);

	public static final MethodsValidator OBJECT_VALIDATOR = new MethodsValidator(AdapterRegistry.OBJECT_ADAPTERS);

	static {
		OBJECT_VALIDATOR.addArg(DefaultEnvArgs.ARG_COMPUTER, IComputerAccess.class);
		OBJECT_VALIDATOR.addArg(DefaultEnvArgs.ARG_CONTEXT, ILuaContext.class);
	}

	public static final ComposedMethodsFactory OBJECT_METHODS_FACTORY = new ComposedMethodsFactory(AdapterRegistry.OBJECT_ADAPTERS);

	public static void init() {
		ITypeConvertersRegistry converter = new TypeConversionRegistryCC();
		// CC converter is default one (legacy behaviour)
		TypeConvertersProvider.INSTANCE.registerConverter(Architectures.COMPUTER_CRAFT, converter, true);

		LuaTypeQualifier.registerType(ILuaObject.class, LuaArgType.OBJECT);
	}

	public static void registerProvider() {
		ComputerCraftAPI.registerPeripheralProvider(new PeripheralProvider());
	}
}

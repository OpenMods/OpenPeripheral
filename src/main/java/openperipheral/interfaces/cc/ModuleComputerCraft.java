package openperipheral.interfaces.cc;

import openperipheral.TypeConvertersProvider;
import openperipheral.adapter.AdapterRegistry;
import openperipheral.adapter.DefaultEnvArgs;
import openperipheral.adapter.composed.ComposedMethodsFactory;
import openperipheral.adapter.composed.MethodSelector;
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

	public static final ComposedMethodsFactory PERIPHERAL_METHODS_FACTORY;

	public static final ComposedMethodsFactory OBJECT_METHODS_FACTORY;

	static {
		final MethodSelector peripheralSelector = new MethodSelector(Architectures.COMPUTER_CRAFT)
				.addDefaultEnv()
				.addProvidedEnv(DefaultEnvArgs.ARG_COMPUTER, IComputerAccess.class)
				.addProvidedEnv(DefaultEnvArgs.ARG_CONTEXT, ILuaContext.class);

		PERIPHERAL_METHODS_FACTORY = new ComposedMethodsFactory(AdapterRegistry.PERIPHERAL_ADAPTERS, peripheralSelector);

		final MethodSelector objectSelector = new MethodSelector(Architectures.COMPUTER_CRAFT)
				.addDefaultEnv()
				.addProvidedEnv(DefaultEnvArgs.ARG_CONTEXT, ILuaContext.class);

		OBJECT_METHODS_FACTORY = new ComposedMethodsFactory(AdapterRegistry.OBJECT_ADAPTERS, objectSelector);
	}

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

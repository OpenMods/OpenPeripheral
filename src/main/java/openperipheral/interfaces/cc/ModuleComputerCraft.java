package openperipheral.interfaces.cc;

import openperipheral.ApiProvider;
import openperipheral.adapter.AdapterRegistry;
import openperipheral.adapter.composed.ComposedMethodsFactory;
import openperipheral.adapter.composed.MethodSelector;
import openperipheral.adapter.method.LuaTypeQualifier;
import openperipheral.api.*;
import openperipheral.converter.TypeConvertersProvider;
import openperipheral.interfaces.cc.providers.AdapterFactoryWrapper;
import openperipheral.interfaces.cc.providers.PeripheralProvider;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class ModuleComputerCraft {

	public static final ComposedMethodsFactory PERIPHERAL_METHODS_FACTORY;

	public static final ComposedMethodsFactory OBJECT_METHODS_FACTORY;

	static {
		final MethodSelector peripheralSelector = new MethodSelector(Constants.ARCH_COMPUTER_CRAFT)
				.addDefaultEnv()
				.addProvidedEnv(Constants.ARG_ACCESS, IArchitectureAccess.class)
				.addProvidedEnv(Constants.ARG_COMPUTER, IComputerAccess.class)
				.addProvidedEnv(Constants.ARG_CONTEXT, ILuaContext.class);

		PERIPHERAL_METHODS_FACTORY = new ComposedMethodsFactory(AdapterRegistry.PERIPHERAL_ADAPTERS, peripheralSelector);

		final MethodSelector objectSelector = new MethodSelector(Constants.ARCH_COMPUTER_CRAFT)
				.addDefaultEnv()
				.addProvidedEnv(Constants.ARG_CONTEXT, ILuaContext.class);

		OBJECT_METHODS_FACTORY = new ComposedMethodsFactory(AdapterRegistry.OBJECT_ADAPTERS, objectSelector);
	}

	public static void init() {
		ITypeConvertersRegistry converter = new TypeConversionRegistryCC();
		// CC converter is default one (legacy behaviour)
		TypeConvertersProvider.INSTANCE.registerConverter(Constants.ARCH_COMPUTER_CRAFT, converter);

		LuaTypeQualifier.registerType(ILuaObject.class, LuaArgType.OBJECT);
	}

	public static void registerProvider() {
		ComputerCraftAPI.registerPeripheralProvider(new PeripheralProvider());
	}

	public static void installAPI(ApiProvider apiProvider) {
		apiProvider.registerClass(AdapterFactoryWrapper.class);
	}
}

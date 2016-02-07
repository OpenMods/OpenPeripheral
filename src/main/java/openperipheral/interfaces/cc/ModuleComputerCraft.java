package openperipheral.interfaces.cc;

import java.util.Map;

import openmods.access.ApiProviderRegistry;
import openperipheral.CommandDump;
import openperipheral.adapter.AdapterRegistry;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.composed.ComposedMethodsFactory;
import openperipheral.adapter.composed.IndexedMethodMap;
import openperipheral.adapter.composed.MethodSelector;
import openperipheral.adapter.types.SingleArgType;
import openperipheral.adapter.types.classifier.TypeClassifier;
import openperipheral.api.Constants;
import openperipheral.api.IApiInterface;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.converter.IConverter;
import openperipheral.converter.TypeConvertersProvider;
import openperipheral.interfaces.cc.providers.AdapterFactoryWrapperCC;
import openperipheral.interfaces.cc.providers.PeripheralProvider;
import openperipheral.util.DocBuilder;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class ModuleComputerCraft {

	public static ComposedMethodsFactory<IndexedMethodMap> PERIPHERAL_METHODS_FACTORY;

	public static ComposedMethodsFactory<IndexedMethodMap> OBJECT_METHODS_FACTORY;

	public static ComputerCraftEnv ENV;

	public static void init() {
		final MethodSelector peripheralSelector = new MethodSelector(Constants.ARCH_COMPUTER_CRAFT)
				.allowReturnSignal()
				.addDefaultEnv()
				.addProvidedEnv(Constants.ARG_ACCESS, IArchitectureAccess.class)
				.addProvidedEnv(Constants.ARG_COMPUTER, IComputerAccess.class)
				.addProvidedEnv(Constants.ARG_CONTEXT, ILuaContext.class);

		PERIPHERAL_METHODS_FACTORY = new ComposedMethodsFactory<IndexedMethodMap>(AdapterRegistry.PERIPHERAL_ADAPTERS, peripheralSelector) {
			@Override
			protected IndexedMethodMap wrapMethods(Class<?> targetCls, Map<String, IMethodExecutor> methods) {
				return new IndexedMethodMap(methods);
			}
		};

		// can't push events, so not allowing return signals
		final MethodSelector objectSelector = new MethodSelector(Constants.ARCH_COMPUTER_CRAFT)
				.addDefaultEnv()
				.addProvidedEnv(Constants.ARG_CONTEXT, ILuaContext.class);

		OBJECT_METHODS_FACTORY = new ComposedMethodsFactory<IndexedMethodMap>(AdapterRegistry.OBJECT_ADAPTERS, objectSelector) {
			@Override
			protected IndexedMethodMap wrapMethods(Class<?> targetCls, Map<String, IMethodExecutor> methods) {
				return new IndexedMethodMap(methods);
			}
		};

		CommandDump.addArchSerializer("ComputerCraft", "peripheral", DocBuilder.TILE_ENTITY_DECORATOR, PERIPHERAL_METHODS_FACTORY);
		CommandDump.addArchSerializer("ComputerCraft", "object", DocBuilder.SCRIPT_OBJECT_DECORATOR, OBJECT_METHODS_FACTORY);

		final IConverter converter = new TypeConversionRegistryCC();
		// CC converter is default one (legacy behaviour)
		TypeConvertersProvider.INSTANCE.registerConverter(Constants.ARCH_COMPUTER_CRAFT, converter);

		TypeClassifier.INSTANCE.registerType(ILuaObject.class, SingleArgType.OBJECT);

		ENV = new ComputerCraftEnv(converter);
	}

	public static void registerProvider() {
		ComputerCraftAPI.registerPeripheralProvider(new PeripheralProvider());
	}

	public static void installAPI(ApiProviderRegistry<IApiInterface> registry) {
		registry.registerClass(AdapterFactoryWrapperCC.class);
	}
}

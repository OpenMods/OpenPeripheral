package openperipheral.interfaces.oc;

import java.util.Map;

import li.cil.oc.api.Driver;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.machine.Value;
import openperipheral.ApiProvider;
import openperipheral.CommandDump;
import openperipheral.adapter.AdapterRegistry;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.composed.*;
import openperipheral.adapter.method.LuaTypeQualifier;
import openperipheral.api.Constants;
import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.converter.IConverter;
import openperipheral.converter.TypeConvertersProvider;
import openperipheral.interfaces.oc.providers.AdapterFactoryWrapperOC;
import openperipheral.interfaces.oc.providers.DriverOpenPeripheral;
import openperipheral.util.DocBuilder;

public class ModuleOpenComputers {

	public static final ComposedMethodsFactory<NamedMethodMap> OBJECT_METHODS_FACTORY;

	public static final ComposedMethodsFactory<IndexedMethodMap> PERIPHERAL_METHODS_FACTORY;

	static {
		final MethodSelector peripheralSelector = new MethodSelector(Constants.ARCH_OPEN_COMPUTERS)
				.addDefaultEnv()
				.addProvidedEnv(Constants.ARG_ACCESS, IArchitectureAccess.class)
				.addProvidedEnv(Constants.ARG_CONTEXT, Context.class);

		PERIPHERAL_METHODS_FACTORY = new ComposedMethodsFactory<IndexedMethodMap>(AdapterRegistry.PERIPHERAL_ADAPTERS, peripheralSelector) {
			@Override
			protected IndexedMethodMap wrapMethods(Map<String, IMethodExecutor> methods) {
				return new IndexedMethodMap(methods);
			}
		};

		final MethodSelector objectSelector = new MethodSelector(Constants.ARCH_OPEN_COMPUTERS)
				.addDefaultEnv()
				.addProvidedEnv(Constants.ARG_CONTEXT, Context.class);

		OBJECT_METHODS_FACTORY = new ComposedMethodsFactory<NamedMethodMap>(AdapterRegistry.OBJECT_ADAPTERS, objectSelector) {
			@Override
			protected NamedMethodMap wrapMethods(Map<String, IMethodExecutor> methods) {
				return new NamedMethodMap(methods);
			}
		};

		CommandDump.addArchSerializer("OpenComputers", "peripheral", DocBuilder.TILE_ENTITY_DECORATOR, PERIPHERAL_METHODS_FACTORY);
		CommandDump.addArchSerializer("OpenComputers", "object", DocBuilder.NULL_DECORATOR, OBJECT_METHODS_FACTORY);
	}

	public static void init() {
		IConverter converter = new TypeConversionRegistryOC();
		TypeConvertersProvider.INSTANCE.registerConverter(Constants.ARCH_OPEN_COMPUTERS, converter);

		LuaTypeQualifier.registerType(Value.class, ArgType.OBJECT);
	}

	public static void registerProvider() {
		Driver.add(new DriverOpenPeripheral());
	}

	public static void installAPI(ApiProvider apiProvider) {
		apiProvider.registerClass(AdapterFactoryWrapperOC.class);
	}
}

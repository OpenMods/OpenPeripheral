package openperipheral.interfaces.oc;

import li.cil.oc.api.Driver;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.machine.Value;
import li.cil.oc.api.network.ManagedEnvironment;
import openmods.injector.InjectedClassesManager;
import openperipheral.ApiProvider;
import openperipheral.CommandDump;
import openperipheral.adapter.AdapterRegistry;
import openperipheral.adapter.TypeQualifier;
import openperipheral.adapter.composed.ComposedMethodsFactory;
import openperipheral.adapter.composed.MethodSelector;
import openperipheral.adapter.types.SingleArgType;
import openperipheral.api.Constants;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.converter.IConverter;
import openperipheral.converter.TypeConvertersProvider;
import openperipheral.interfaces.oc.asm.object.ObjectCodeGenerator;
import openperipheral.interfaces.oc.asm.peripheral.PeripheralCodeGenerator;
import openperipheral.interfaces.oc.providers.*;
import openperipheral.util.DocBuilder;

import com.google.common.base.Preconditions;

public class ModuleOpenComputers {

	private static final String PERIPHERAL_CLASS_PREFIX = "OP_OC_Peripheral";
	private static final String OBJECT_CLASS_PREFIX = "OP_OC_Object";

	public static final ComposedMethodsFactory<IEnviromentInstanceWrapper<Value>> OBJECT_METHODS_FACTORY;

	public static final ComposedMethodsFactory<IEnviromentInstanceWrapper<ManagedEnvironment>> PERIPHERAL_METHODS_FACTORY;

	static {
		final MethodSelector peripheralSelector = new MethodSelector(Constants.ARCH_OPEN_COMPUTERS)
				.addDefaultEnv()
				.addProvidedEnv(Constants.ARG_ACCESS, IArchitectureAccess.class)
				.addProvidedEnv(Constants.ARG_CONTEXT, Context.class);

		PERIPHERAL_METHODS_FACTORY = new EnvironmentMethodsFactory<ManagedEnvironment>(
				AdapterRegistry.PERIPHERAL_ADAPTERS,
				peripheralSelector,
				PERIPHERAL_CLASS_PREFIX,
				new PeripheralCodeGenerator());

		InjectedClassesManager.instance.registerProvider(PERIPHERAL_CLASS_PREFIX, new EnvironmentClassBytesProvider<ManagedEnvironment>(PERIPHERAL_METHODS_FACTORY));

		final MethodSelector objectSelector = new MethodSelector(Constants.ARCH_OPEN_COMPUTERS)
				.addDefaultEnv()
				.addProvidedEnv(Constants.ARG_CONTEXT, Context.class);

		OBJECT_METHODS_FACTORY = new EnvironmentMethodsFactory<Value>(
				AdapterRegistry.OBJECT_ADAPTERS,
				objectSelector,
				OBJECT_CLASS_PREFIX,
				new ObjectCodeGenerator()
				);

		InjectedClassesManager.instance.registerProvider(OBJECT_CLASS_PREFIX, new EnvironmentClassBytesProvider<Value>(OBJECT_METHODS_FACTORY));

		CommandDump.addArchSerializer("OpenComputers", "peripheral", DocBuilder.TILE_ENTITY_DECORATOR, PERIPHERAL_METHODS_FACTORY);
		CommandDump.addArchSerializer("OpenComputers", "object", DocBuilder.NULL_DECORATOR, OBJECT_METHODS_FACTORY);
	}

	public static void init() {
		IConverter converter = new TypeConversionRegistryOC();
		TypeConvertersProvider.INSTANCE.registerConverter(Constants.ARCH_OPEN_COMPUTERS, converter);

		TypeQualifier.INSTANCE.registerType(Value.class, SingleArgType.OBJECT);
	}

	public static void registerProvider() {
		Driver.add(new DriverOpenPeripheral());
	}

	public static void installAPI(ApiProvider apiProvider) {
		apiProvider.registerClass(AdapterFactoryWrapperOC.class);
	}

	public static Value wrapObject(Object target) {
		Preconditions.checkNotNull(target, "Can't wrap null");
		IEnviromentInstanceWrapper<Value> wrapper = OBJECT_METHODS_FACTORY.getAdaptedClass(target.getClass());
		return wrapper.isEmpty()? null : wrapper.createEnvironment(target);
	}
}

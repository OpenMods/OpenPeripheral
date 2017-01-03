package openperipheral.interfaces.oc;

import com.google.common.base.Preconditions;
import li.cil.oc.api.Driver;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.machine.Value;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Node;
import openmods.access.ApiProviderRegistry;
import openmods.injector.InjectedClassesManager;
import openperipheral.CommandDump;
import openperipheral.adapter.AdapterRegistry;
import openperipheral.adapter.composed.ComposedMethodsFactory;
import openperipheral.adapter.composed.MethodSelector;
import openperipheral.adapter.types.SingleArgType;
import openperipheral.adapter.types.classifier.TypeClassifier;
import openperipheral.api.Constants;
import openperipheral.api.IApiInterface;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.converter.IConverter;
import openperipheral.converter.TypeConvertersProvider;
import openperipheral.interfaces.oc.asm.object.ObjectCodeGenerator;
import openperipheral.interfaces.oc.asm.peripheral.PeripheralCodeGenerator;
import openperipheral.interfaces.oc.providers.AdapterFactoryWrapperOC;
import openperipheral.interfaces.oc.providers.DriverOpenPeripheral;
import openperipheral.interfaces.oc.providers.EnvironmentClassBytesProvider;
import openperipheral.interfaces.oc.providers.EnvironmentMethodsFactory;
import openperipheral.interfaces.oc.providers.IEnviromentInstanceWrapper;
import openperipheral.util.DocBuilder;

public class ModuleOpenComputers {

	private static final String PERIPHERAL_CLASS_PREFIX = "OP_OC_Peripheral";
	private static final String OBJECT_CLASS_PREFIX = "OP_OC_Object";

	public static ComposedMethodsFactory<IEnviromentInstanceWrapper<Value>> OBJECT_METHODS_FACTORY;

	public static ComposedMethodsFactory<IEnviromentInstanceWrapper<ManagedEnvironment>> PERIPHERAL_METHODS_FACTORY;

	public static OpenComputersEnv ENV;

	public static void init() {
		final MethodSelector peripheralSelector = new MethodSelector(Constants.ARCH_OPEN_COMPUTERS)
				.allowReturnSignal()
				.addDefaultEnv()
				.addProvidedEnv(Constants.ARG_ACCESS, IArchitectureAccess.class)
				.addProvidedEnv(Constants.ARG_CONTEXT, Context.class)
				.addProvidedEnv(Constants.ARG_NODE, Node.class);

		PERIPHERAL_METHODS_FACTORY = new EnvironmentMethodsFactory<ManagedEnvironment>(
				AdapterRegistry.PERIPHERAL_ADAPTERS,
				peripheralSelector,
				PERIPHERAL_CLASS_PREFIX,
				new PeripheralCodeGenerator());

		InjectedClassesManager.instance.registerProvider(PERIPHERAL_CLASS_PREFIX, new EnvironmentClassBytesProvider<ManagedEnvironment>(PERIPHERAL_METHODS_FACTORY));

		final MethodSelector objectSelector = new MethodSelector(Constants.ARCH_OPEN_COMPUTERS)
				// .allowReturnSignal() // for symmetry with CC
				.addDefaultEnv()
				.addProvidedEnv(Constants.ARG_CONTEXT, Context.class);

		OBJECT_METHODS_FACTORY = new EnvironmentMethodsFactory<Value>(
				AdapterRegistry.OBJECT_ADAPTERS,
				objectSelector,
				OBJECT_CLASS_PREFIX,
				new ObjectCodeGenerator());

		InjectedClassesManager.instance.registerProvider(OBJECT_CLASS_PREFIX, new EnvironmentClassBytesProvider<Value>(OBJECT_METHODS_FACTORY));

		CommandDump.addArchSerializer("OpenComputers", "peripheral", DocBuilder.TILE_ENTITY_DECORATOR, PERIPHERAL_METHODS_FACTORY);
		CommandDump.addArchSerializer("OpenComputers", "object", DocBuilder.SCRIPT_OBJECT_DECORATOR, OBJECT_METHODS_FACTORY);

		final IConverter converter = new TypeConversionRegistryOC();
		TypeConvertersProvider.INSTANCE.registerConverter(Constants.ARCH_OPEN_COMPUTERS, converter);

		TypeClassifier.INSTANCE.registerType(Value.class, SingleArgType.OBJECT);

		ENV = new OpenComputersEnv(converter);
	}

	public static void registerProvider() {
		Driver.add(new DriverOpenPeripheral());
	}

	public static void installAPI(ApiProviderRegistry<IApiInterface> registry) {
		registry.registerClass(AdapterFactoryWrapperOC.class);
	}

	public static Value wrapObject(Object target) {
		Preconditions.checkNotNull(target, "Can't wrap null");
		IEnviromentInstanceWrapper<Value> wrapper = OBJECT_METHODS_FACTORY.getAdaptedClass(target.getClass());
		return wrapper.isEmpty()? null : wrapper.createEnvironment(target);
	}
}

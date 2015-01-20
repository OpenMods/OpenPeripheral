package openperipheral.interfaces.oc;

import li.cil.oc.api.Driver;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.machine.Value;
import openperipheral.TypeConvertersProvider;
import openperipheral.adapter.*;
import openperipheral.adapter.method.LuaTypeQualifier;
import openperipheral.api.Architectures;
import openperipheral.api.ITypeConvertersRegistry;
import openperipheral.api.LuaArgType;
import openperipheral.interfaces.oc.providers.DriverOpenPeripheral;

public class ModuleOpenComputers {

	public static final MethodsValidator PERIPHERAL_VALIDATOR = new MethodsValidator(AdapterRegistry.PERIPHERAL_ADAPTERS);

	static {
		PERIPHERAL_VALIDATOR.addArg(DefaultEnvArgs.ARG_CONTEXT, Context.class);
	}

	public static final ComposedMethodsFactory PERIPHERAL_METHODS_FACTORY = new ComposedMethodsFactory(AdapterRegistry.PERIPHERAL_ADAPTERS);

	public static final MethodsValidator OBJECT_VALIDATOR = new MethodsValidator(AdapterRegistry.OBJECT_ADAPTERS);

	static {
		OBJECT_VALIDATOR.addArg(DefaultEnvArgs.ARG_CONTEXT, Context.class);
	}

	public static final ComposedMethodsFactory OBJECT_METHODS_FACTORY = new ComposedMethodsFactory(AdapterRegistry.OBJECT_ADAPTERS);

	public static void init() {
		ITypeConvertersRegistry converter = new TypeConversionRegistryOC();
		TypeConvertersProvider.INSTANCE.registerConverter(Architectures.OPEN_COMPUTERS, converter, false);

		LuaTypeQualifier.registerType(Value.class, LuaArgType.OBJECT);
	}

	public static void registerProvider() {
		Driver.add(new DriverOpenPeripheral());
	}
}

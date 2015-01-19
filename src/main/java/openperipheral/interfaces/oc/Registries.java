package openperipheral.interfaces.oc;

import li.cil.oc.api.machine.Context;
import openperipheral.adapter.*;

public class Registries {

	public static final MethodsValidator PERIPHERAL_VALIDATOR = new MethodsValidator(AdapterRegistry.PERIPHERAL_ADAPTERS);

	static {
		PERIPHERAL_VALIDATOR.addArg(DefaultArgNames.ARG_CONTEXT, Context.class);
	}

	public static final ComposedMethodsFactory PERIPHERAL_METHODS_FACTORY = new ComposedMethodsFactory(AdapterRegistry.PERIPHERAL_ADAPTERS);

	public static final MethodsValidator OBJECT_VALIDATOR = new MethodsValidator(AdapterRegistry.OBJECT_ADAPTERS);

	static {
		OBJECT_VALIDATOR.addArg(DefaultArgNames.ARG_CONTEXT, Context.class);
	}

	public static final ComposedMethodsFactory OBJECT_METHODS_FACTORY = new ComposedMethodsFactory(AdapterRegistry.OBJECT_ADAPTERS);

}

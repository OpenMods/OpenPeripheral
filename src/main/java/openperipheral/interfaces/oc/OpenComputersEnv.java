package openperipheral.interfaces.oc;

import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Node;
import openperipheral.adapter.IMethodCall;
import openperipheral.api.Constants;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.converter.IConverter;
import openperipheral.converter.TypeConvertersProvider;

public class OpenComputersEnv {

	public static IArchitectureAccess createAccess(final Node ownNode, final Context context) {
		return new IArchitectureAccess() {
			@Override
			public String architecture() {
				return Constants.ARCH_OPEN_COMPUTERS;
			}

			@Override
			public String callerName() {
				return context.node().address();
			}

			@Override
			public String peripheralName() {
				return ownNode.address();
			}

			@Override
			public boolean signal(String name, Object... args) {
				return context.signal(name, args);
			}

			@Override
			public Object wrapObject(Object target) {
				return ModuleOpenComputers.wrapObject(target);
			}
		};
	}

	public static IMethodCall addCommonArgs(IMethodCall call, Context context) {
		final IConverter converter = TypeConvertersProvider.INSTANCE.getConverter(Constants.ARCH_OPEN_COMPUTERS);
		return call
				.setOptionalArg(Constants.ARG_CONVERTER, converter)
				.setOptionalArg(Constants.ARG_CONTEXT, context);
	}

	public static IMethodCall addPeripheralArgs(IMethodCall call, Node node, Context context) {
		final IArchitectureAccess wrappedAccess = createAccess(node, context);
		return addCommonArgs(call, context).setOptionalArg(Constants.ARG_ACCESS, wrappedAccess);
	}
}

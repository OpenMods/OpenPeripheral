package openperipheral.interfaces.oc;

import li.cil.oc.api.machine.Context;
import openperipheral.adapter.IMethodCall;
import openperipheral.api.Constants;
import openperipheral.api.IArchitectureAccess;
import openperipheral.api.ITypeConvertersRegistry;
import openperipheral.converter.TypeConvertersProvider;
import openperipheral.interfaces.oc.wrappers.ManagedPeripheralWrapper;

public class OpenComputersEnv {

	private static IArchitectureAccess createAccess(final Context context) {
		return new IArchitectureAccess() {
			@Override
			public String architecture() {
				return Constants.ARCH_OPEN_COMPUTERS;
			}

			@Override
			public String peripheralName() {
				return context.node().address();
			}

			@Override
			public boolean signal(String name, Object... args) {
				return context.signal(name, args);
			}

			@Override
			public Object wrapObject(Object target) {
				return ManagedPeripheralWrapper.wrap(target);
			}
		};
	}

	public static IMethodCall addCommonArgs(IMethodCall call) {
		final ITypeConvertersRegistry converter = TypeConvertersProvider.INSTANCE.getConverter(Constants.ARCH_OPEN_COMPUTERS);
		return call.setOptionalArg(Constants.ARG_CONVERTER, converter);
	}

	public static IMethodCall addPeripheralArgs(IMethodCall call, Context context) {
		final IArchitectureAccess wrappedAccess = createAccess(context);
		return addCommonArgs(call).setOptionalArg(Constants.ARG_ACCESS, wrappedAccess);
	}
}

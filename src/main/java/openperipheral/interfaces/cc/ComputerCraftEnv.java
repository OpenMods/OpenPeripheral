package openperipheral.interfaces.cc;

import openperipheral.adapter.IMethodCall;
import openperipheral.api.Constants;
import openperipheral.api.IArchitectureAccess;
import openperipheral.api.ITypeConvertersRegistry;
import openperipheral.converter.TypeConvertersProvider;
import openperipheral.interfaces.cc.wrappers.LuaObjectWrapper;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class ComputerCraftEnv {

	private static IArchitectureAccess createAccess(final IComputerAccess access) {
		return new IArchitectureAccess() {
			@Override
			public String architecture() {
				return Constants.ARCH_COMPUTER_CRAFT;
			}

			@Override
			public String peripheralName() {
				return access.getAttachmentName();
			}

			@Override
			public boolean signal(String name, Object... args) {
				access.queueEvent(name, args);
				return true;
			}

			@Override
			public Object wrapObject(Object target) {
				return LuaObjectWrapper.wrap(target);
			}
		};
	}

	public static IMethodCall addCommonArgs(IMethodCall call) {
		final ITypeConvertersRegistry converter = TypeConvertersProvider.INSTANCE.getConverter(Constants.ARCH_COMPUTER_CRAFT);
		return call.setOptionalArg(Constants.ARG_CONVERTER, converter);
	}

	public static IMethodCall addPeripheralArgs(IMethodCall call, IComputerAccess access) {
		final IArchitectureAccess wrappedAccess = createAccess(access);
		return addCommonArgs(call).setOptionalArg(Constants.ARG_ACCESS, wrappedAccess);
	}
}

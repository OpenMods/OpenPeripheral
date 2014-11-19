package openperipheral.adapter.peripheral;

import openperipheral.adapter.AdapterManager;
import openperipheral.adapter.IDescriptable;
import openperipheral.adapter.composed.ClassMethodsListBuilder;
import openperipheral.adapter.method.MethodDeclaration;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class PeripheralMethodsListBuilder extends ClassMethodsListBuilder<IPeripheralMethodExecutor> {

	public PeripheralMethodsListBuilder() {
		super(AdapterManager.PERIPHERALS_MANAGER);
	}

	public static final String ARG_TARGET = "target";

	@Override
	public IPeripheralMethodExecutor createDummyWrapper(final Object lister, final MethodDeclaration method) {
		return new IPeripheralMethodExecutor() {
			@Override
			public IDescriptable description() {
				return method;
			}

			@Override
			public Object[] execute(IComputerAccess computer, ILuaContext context, Object target, Object[] args) throws Exception {
				return method.createWrapper(lister).setJavaArg(ARG_TARGET, target).setLuaArgs(args).call();
			}
		};
	}
}
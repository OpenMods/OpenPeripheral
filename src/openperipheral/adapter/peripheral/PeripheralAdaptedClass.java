package openperipheral.adapter.peripheral;

import openperipheral.adapter.AdaptedClass;
import openperipheral.adapter.AdapterManager;
import openperipheral.adapter.IDescriptable;
import openperipheral.adapter.method.MethodDeclaration;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.lua.ILuaContext;

public class PeripheralAdaptedClass extends AdaptedClass<IPeripheralMethodExecutor> {

	public PeripheralAdaptedClass(AdapterManager<?, IPeripheralMethodExecutor> manager, Class<?> cls) {
		super(manager, cls);
	}

	@Override
	public IPeripheralMethodExecutor createDummyWrapper(final Object lister, final MethodDeclaration method) {
		return new IPeripheralMethodExecutor() {
			@Override
			public IDescriptable getWrappedMethod() {
				return method;
			}

			@Override
			public Object[] execute(IComputerAccess computer, ILuaContext context, Object target, Object[] args) throws Exception {
				return method.createWrapper(lister).setJavaArg(ARG_TARGET, target).setLuaArgs(args).call();
			}

			@Override
			public boolean isSynthetic() {
				return true;
			}
		};
	}
}
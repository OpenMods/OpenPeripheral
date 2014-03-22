package openperipheral.adapter.object;

import openperipheral.adapter.AdaptedClass;
import openperipheral.adapter.AdapterManager;
import openperipheral.adapter.IDescriptable;
import openperipheral.adapter.method.MethodDeclaration;
import dan200.computercraft.api.lua.ILuaContext;

public class ObjectAdaptedClass extends AdaptedClass<IObjectMethodExecutor> {

	public ObjectAdaptedClass(AdapterManager<?, IObjectMethodExecutor> manager, Class<?> cls) {
		super(manager, cls);
	}

	@Override
	public IObjectMethodExecutor createDummyWrapper(final Object lister, final MethodDeclaration method) {
		return new IObjectMethodExecutor() {
			@Override
			public IDescriptable getWrappedMethod() {
				return method;
			}

			@Override
			public Object[] execute(ILuaContext context, Object target, Object[] args) throws Exception {
				return method.createWrapper(lister).setJavaArg(ARG_TARGET, target).setLuaArgs(args).call();
			}

			@Override
			public boolean isSynthetic() {
				return true;
			}
		};
	}
}
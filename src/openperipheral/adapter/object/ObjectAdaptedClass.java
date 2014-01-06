package openperipheral.adapter.object;

import openperipheral.adapter.AdaptedClass;
import openperipheral.adapter.AdapterManager;
import openperipheral.adapter.MethodDeclaration;
import dan200.computer.api.ILuaContext;

public class ObjectAdaptedClass extends AdaptedClass<IObjectMethodExecutor> {

	public ObjectAdaptedClass(AdapterManager<?, IObjectMethodExecutor> manager, Class<?> cls) {
		super(manager, cls);
	}

	@Override
	public IObjectMethodExecutor createDummyWrapper(final Object lister, final MethodDeclaration method) {
		return new IObjectMethodExecutor() {
			@Override
			public MethodDeclaration getWrappedMethod() {
				return method;
			}

			@Override
			public Object[] execute(ILuaContext context, Object target, Object[] args) throws Exception {
				return method.createWrapper(lister).setLuaArgs(args).call();
			}

			@Override
			public boolean isSynthetic() {
				return true;
			}
		};
	}
}
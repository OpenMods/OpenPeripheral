package openperipheral.adapter.object;

import openperipheral.adapter.AdapterManager;
import openperipheral.adapter.composed.ClassMethodsList;

import com.google.common.base.Preconditions;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;

public class LuaObjectWrapper {
	public static ILuaObject wrap(AdapterManager<?, IObjectMethodExecutor> manager, Object target) {
		Preconditions.checkNotNull(target, "Can't wrap null");
		ClassMethodsList<IObjectMethodExecutor> adapted = manager.getAdapterClass(target.getClass());
		return wrap(adapted, target);
	}

	public static ILuaObject wrap(final ClassMethodsList<IObjectMethodExecutor> adapted, final Object target) {
		return new ILuaObject() {

			@Override
			public String[] getMethodNames() {
				return adapted.methodNames;
			}

			@Override
			public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception {
				IObjectMethodExecutor executor = adapted.getMethod(method);
				Preconditions.checkNotNull(executor, "Invalid method index %s for wrapped %s", method, target.getClass());
				return executor.execute(context, target, arguments);
			}
		};
	}
}

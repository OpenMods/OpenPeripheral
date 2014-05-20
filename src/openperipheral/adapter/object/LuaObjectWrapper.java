package openperipheral.adapter.object;

import java.util.Arrays;
import java.util.logging.Level;

import openmods.Log;
import openperipheral.adapter.AdapterManager;
import openperipheral.adapter.WrappedException;
import openperipheral.adapter.composed.ClassMethodsList;

import com.google.common.base.Preconditions;

import dan200.computer.api.ILuaContext;
import dan200.computer.api.ILuaObject;

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
				Preconditions.checkArgument(executor != null, "Invalid method index: %d", method);

				try {
					return executor.execute(context, target, arguments);
				} catch (WrappedException e) {
					String methodName = adapted.methodNames[method];
					Log.log(Level.FINE, e.getCause(), "Adapter error during method %s(%d) execution on object %s, args: %s",
							methodName, method, target.getClass(), Arrays.toString(arguments));
					throw e;
				} catch (Exception e) {
					String methodName = adapted.methodNames[method];
					Log.log(Level.FINE, e.getCause(), "Internal error during method %s(%d) execution on object %s, args: %s",
							methodName, method, target.getClass(), Arrays.toString(arguments));

					// can wrap here, no special exceptions
					throw new WrappedException(e);
				}
			}
		};
	}
}

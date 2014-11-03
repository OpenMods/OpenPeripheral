package openperipheral.adapter.object;

import java.util.Arrays;

import openmods.Log;
import openperipheral.adapter.AdapterLogicException;
import openperipheral.adapter.AdapterManager;
import openperipheral.adapter.composed.ClassMethodsList;

import org.apache.logging.log4j.Level;

import com.google.common.base.Preconditions;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;

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
			public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
				IObjectMethodExecutor executor = adapted.getMethod(method);
				Preconditions.checkNotNull(executor, "Invalid method index: %d", method);

				try {
					return executor.execute(context, target, arguments);
				} catch (LuaException e) {
					throw e;
				} catch (InterruptedException e) {
					throw e;
				} catch (Throwable t) {
					String methodName = adapted.methodNames[method];
					Log.log(Level.DEBUG, t.getCause(), "Internal error during method %s(%d) execution on object %s, args: %s",
							methodName, method, target.getClass(), Arrays.toString(arguments));

					throw new AdapterLogicException(t).rethrow();
				}
			}
		};
	}
}

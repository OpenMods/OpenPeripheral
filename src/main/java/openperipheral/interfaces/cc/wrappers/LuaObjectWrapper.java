package openperipheral.interfaces.cc.wrappers;

import java.util.Arrays;

import openmods.Log;
import openperipheral.adapter.AdapterLogicException;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.composed.IndexedMethodMap;
import openperipheral.api.adapter.GenerationFailedException;
import openperipheral.interfaces.cc.ComputerCraftEnv;
import openperipheral.interfaces.cc.ModuleComputerCraft;
import openperipheral.interfaces.cc.SynchronousExecutor;

import org.apache.logging.log4j.Level;

import com.google.common.base.Preconditions;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;

public class LuaObjectWrapper {

	private static class WrappedLuaObject implements ILuaObject {
		private final IndexedMethodMap methods;
		private final Object target;

		private WrappedLuaObject(IndexedMethodMap methods, Object target) {
			this.methods = methods;
			this.target = target;
		}

		@Override
		public String[] getMethodNames() {
			return methods.getMethodNames();
		}

		private Object[] call(int methodIndex, IMethodExecutor executor, ILuaContext context, Object[] arguments) throws LuaException, InterruptedException {
			try {
				return ComputerCraftEnv.addCommonArgs(executor.startCall(target), context).call(arguments);
			} catch (InterruptedException e) {
				throw e;
			} catch (LuaException e) {
				throw e;
			} catch (Throwable e) {
				String methodName = methods.getMethodName(methodIndex);
				Log.log(Level.DEBUG, e.getCause(), "Internal error during method %s(%d) execution on object %s, args: %s",
						methodName, methodIndex, target.getClass(), Arrays.toString(arguments));
				throw new LuaException(AdapterLogicException.getMessageForThrowable(e));
			}
		}

		@Override
		public Object[] callMethod(final ILuaContext context, final int index, final Object[] arguments) throws LuaException, InterruptedException {
			final IMethodExecutor method = methods.getMethod(index);
			Preconditions.checkNotNull(method, "Invalid method index: %d", index);

			if (method.isAsynchronous()) return call(index, method, context, arguments);
			else {
				Object[] result = SynchronousExecutor.executeInMainThread(context, new SynchronousExecutor.Task() {
					@Override
					public Object[] execute() throws LuaException, InterruptedException {
						return call(index, method, context, arguments);
					}
				});
				return result;
			}
		}
	}

	public static ILuaObject wrap(Object target) {
		Preconditions.checkNotNull(target, "Can't wrap null");

		try {
			IndexedMethodMap methods = ModuleComputerCraft.OBJECT_METHODS_FACTORY.getAdaptedClass(target.getClass());
			return methods.isEmpty()? null : new WrappedLuaObject(methods, target);
		} catch (Throwable t) {
			throw new GenerationFailedException(String.format("%s (%s)", target, target.getClass()), t);
		}
	}
}

package openperipheral.interfaces.cc.wrappers;

import java.util.Arrays;
import java.util.Map;

import openmods.Log;
import openperipheral.adapter.*;
import openperipheral.api.Architectures;
import openperipheral.interfaces.cc.ModuleComputerCraft;

import org.apache.logging.log4j.Level;

import com.google.common.base.Preconditions;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;

public class LuaObjectWrapper {

	private static class WrappedLuaObject extends WrappedEntityBase implements ILuaObject {
		private final Object target;

		private WrappedLuaObject(Map<String, IMethodExecutor> methods, Object target) {
			super(methods);
			this.target = target;
		}

		@Override
		public String[] getMethodNames() {
			return super.getMethodNames();
		}

		@Override
		public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			IMethodExecutor executor = getMethod(method);
			Preconditions.checkNotNull(executor, "Invalid method index: %d", method);

			try {
				return DefaultEnvArgs.addCommonArgs(executor.startCall(target), Architectures.COMPUTER_CRAFT)
						.setOptionalArg(DefaultEnvArgs.ARG_CONTEXT, context)
						.call(arguments);
			} catch (LuaException e) {
				throw e;
			} catch (InterruptedException e) {
				throw e;
			} catch (Throwable t) {
				String methodName = getMethodName(method);
				Log.log(Level.DEBUG, t.getCause(), "Internal error during method %s(%d) execution on object %s, args: %s",
						methodName, method, target.getClass(), Arrays.toString(arguments));

				throw new AdapterLogicException(t);
			}
		}
	}

	public static ILuaObject wrap(Object target) {
		Preconditions.checkNotNull(target, "Can't wrap null");
		Map<String, IMethodExecutor> methods = ModuleComputerCraft.OBJECT_METHODS_FACTORY.getAdaptedClass(target.getClass());
		return methods.isEmpty()? null : new WrappedLuaObject(methods, target);
	}
}

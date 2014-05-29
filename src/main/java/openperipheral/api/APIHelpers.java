package openperipheral.api;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLLog;

public class APIHelpers {

	public static final Logger logger;

	static {
		logger = LogManager.getLogger("OpenPeripheral API");
	}

	public static <A> boolean callWithoutReturn(String klazzName, String methodName, Class<? extends A> argType, A argValue) {
		try {
			Method method = OpenPeripheralAPI.getMethod(klazzName, methodName, argType);
			method.invoke(null, argValue);
			return true;
		} catch (Throwable t) {
			logger.warn(String.format("Exception while calling method '%s'", methodName), t);
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public static <A, T> T callWithReturn(String klazzName, String methodName, Class<? extends A> argType, A argValue, Class<? extends T> returnType) {
		T result;
		try {
			Method method = OpenPeripheralAPI.getMethod(klazzName, methodName, argType);
			result = (T)method.invoke(null, argValue);
		} catch (Throwable t) {
			logger.warn(String.format("Exception while calling method '%s'", methodName), t);
			return null;
		}

		if (result == null || returnType.isInstance(result)) {
			return result;
		} else {
			logger.warn(String.format("Method '%s' return type '%s' cannot be cast to '%s'", methodName, result.getClass(), returnType));
			return null;
		}
	}

}

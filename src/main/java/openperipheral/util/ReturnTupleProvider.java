package openperipheral.util;

import com.google.common.reflect.Reflection;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import openmods.access.ApiImplementation;
import openperipheral.api.adapter.method.IMultipleReturnsHelper;
import openperipheral.api.adapter.method.IReturnTuple;

@ApiImplementation
public class ReturnTupleProvider implements IMultipleReturnsHelper {

	private static class ImplHandler implements InvocationHandler {
		private final Object[] returns;

		public ImplHandler(Object... returns) {
			this.returns = returns;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getDeclaringClass() == IReturnTuple.class)
				return returns;

			throw new UnsupportedOperationException(method.toGenericString());
		}
	}

	private static <T> T createProxy(Class<T> cls, Object... returns) {
		return Reflection.newProxy(cls, new ImplHandler(returns));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T1, T2> IReturnTuple2<T1, T2> wrap(T1 arg1, T2 arg2) {
		return createProxy(IReturnTuple2.class, arg1, arg2);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T1, T2, T3> IReturnTuple3<T1, T2, T3> wrap(T1 arg1, T2 arg2, T3 arg3) {
		return createProxy(IReturnTuple3.class, arg1, arg2, arg3);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T1, T2, T3, T4> IReturnTuple4<T1, T2, T3, T4> wrap(T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
		return createProxy(IReturnTuple4.class, arg1, arg2, arg3, arg4);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T1, T2, T3, T4, T5> IReturnTuple5<T1, T2, T3, T4, T5> wrap(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5) {
		return createProxy(IReturnTuple5.class, arg1, arg2, arg3, arg4, arg5);
	}
}

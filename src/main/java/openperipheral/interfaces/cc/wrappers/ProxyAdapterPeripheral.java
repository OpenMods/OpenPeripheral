package openperipheral.interfaces.cc.wrappers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import openperipheral.adapter.IMethodExecutor;

import com.google.common.base.Throwables;

import dan200.computercraft.api.peripheral.IPeripheral;

public class ProxyAdapterPeripheral extends AdapterPeripheral implements InvocationHandler {

	public ProxyAdapterPeripheral(Map<String, IMethodExecutor> wrapper, Object targetObject) {
		super(wrapper, targetObject);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			if (method.getDeclaringClass() == IPeripheral.class) return method.invoke(this, args);
			return method.invoke(target, args);
		} catch (InvocationTargetException e) {
			Throwable wrapper = e.getCause();
			throw Throwables.propagate(wrapper != null? wrapper : e);
		}
	}

}

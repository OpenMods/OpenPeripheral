package openperipheral.interfaces.cc.wrappers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import openperipheral.adapter.composed.IndexedMethodMap;
import dan200.computercraft.api.peripheral.IPeripheral;

public class ProxyAdapterPeripheral extends AdapterPeripheral implements InvocationHandler {

	public ProxyAdapterPeripheral(IndexedMethodMap methods, Object targetObject) {
		super(methods, targetObject);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			if (method.getDeclaringClass() == IPeripheral.class) return method.invoke(this, args);
			return method.invoke(target, args);
		} catch (InvocationTargetException e) {
			Throwable wrapper = e.getCause();
			throw wrapper != null? wrapper : e;
		}
	}

}

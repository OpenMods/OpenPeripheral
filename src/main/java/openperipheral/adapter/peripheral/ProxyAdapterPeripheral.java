package openperipheral.adapter.peripheral;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import openperipheral.adapter.MethodMap;

import com.google.common.base.Throwables;

import dan200.computercraft.api.peripheral.IPeripheral;

public class ProxyAdapterPeripheral extends AdapterPeripheral implements InvocationHandler {

	public ProxyAdapterPeripheral(MethodMap<IPeripheralMethodExecutor> wrapper, Object targetObject) {
		super(wrapper, targetObject);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			if (method.getDeclaringClass() == IPeripheral.class) return method.invoke(this, args);
			return method.invoke(targetObject, args);
		} catch (InvocationTargetException e) {
			Throwable wrapper = e.getCause();
			throw Throwables.propagate(wrapper != null? wrapper : e);
		}
	}

}

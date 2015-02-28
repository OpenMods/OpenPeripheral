package openperipheral.interfaces.oc.providers;

import openperipheral.adapter.composed.IMethodMap;

public interface IEnviromentInstanceWrapper<T> extends IMethodMap {
	public byte[] getClassBytes();

	public T createEnvironment(Object target);
}

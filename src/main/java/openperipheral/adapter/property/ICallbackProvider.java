package openperipheral.adapter.property;

import openperipheral.api.adapter.IPropertyCallback;

interface ICallbackProvider {
	public IPropertyCallback getCallback(Object target);
}
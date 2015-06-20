package openperipheral.adapter.property;

import openperipheral.api.converter.IConverter;

public interface IPropertyExecutor {
	public Object[] call(IConverter converter, Object target, Object... args);
}
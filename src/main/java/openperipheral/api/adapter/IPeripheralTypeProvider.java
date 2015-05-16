package openperipheral.api.adapter;

import openperipheral.api.IApiInterface;

public interface IPeripheralTypeProvider extends IApiInterface {

	public void setType(Class<?> cls, String type);

	public String getType(Class<?> cls);

	public String generateType(Object target);
}

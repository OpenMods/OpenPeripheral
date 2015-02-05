package openperipheral.interfaces.cc;

import openperipheral.converter.inbound.ConverterCallable;
import openperipheral.interfaces.cc.wrappers.LuaObjectWrapper;

public class ConverterCallableCC extends ConverterCallable {

	@Override
	protected Object wrap(Object o) {
		return LuaObjectWrapper.wrap(o);
	}

}

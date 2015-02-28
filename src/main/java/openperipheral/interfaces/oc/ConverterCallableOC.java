package openperipheral.interfaces.oc;

import openperipheral.converter.inbound.ConverterCallable;

public class ConverterCallableOC extends ConverterCallable {

	@Override
	protected Object wrap(Object o) {
		return ModuleOpenComputers.wrapObject(o);
	}

}

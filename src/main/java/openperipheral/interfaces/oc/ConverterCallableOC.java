package openperipheral.interfaces.oc;

import openperipheral.converter.inbound.ConverterCallable;
import openperipheral.interfaces.oc.wrappers.ManagedPeripheralWrapper;

public class ConverterCallableOC extends ConverterCallable {

	@Override
	protected Object wrap(Object o) {
		return ManagedPeripheralWrapper.wrap(o);
	}

}

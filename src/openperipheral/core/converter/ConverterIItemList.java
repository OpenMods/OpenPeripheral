package openperipheral.core.converter;

import openperipheral.api.ITypeConverter;
import openperipheral.core.TypeConversionRegistry;
import appeng.api.IItemList;

public class ConverterIItemList implements ITypeConverter {

	@Override
	public Object fromLua(Object obj, Class<?> expected) {
		return null;
	}

	@Override
	public Object toLua(Object obj) {
		if (obj instanceof IItemList) { return TypeConversionRegistry.toLua(((IItemList)obj).getItems()); }

		return null;
	}

}

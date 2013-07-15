package openperipheral.core.converter;

import java.util.HashMap;

import appeng.api.IItemList;

import openperipheral.api.ITypeConverter;
import openperipheral.core.TypeConversionRegistry;

public class ConverterIItemList implements ITypeConverter {

	@Override
	public Object fromLua(Object obj, Class expected) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object toLua(Object obj) {
		if (obj instanceof IItemList) {
			return TypeConversionRegistry.toLua(((IItemList)obj).getItems());
		}
		
		return null;
	}

}

package openperipheral.core.converter;

import java.util.HashMap;
import java.util.List;

import openperipheral.api.ITypeConverter;
import openperipheral.core.TypeConversionRegistry;

public class ConverterList implements ITypeConverter {

	@Override
	public Object fromLua(Object obj, Class expected) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object toLua(Object obj) {
		if (obj instanceof List) {
			HashMap ret = new HashMap();
			int index = 1;
			List objList = (List)obj;
			for (int i = 0; i < objList.size(); i++) {
				ret.put(index++, TypeConversionRegistry.toLua(objList.get(i)));
			}
			return ret;
		}
		return null;
	}

}

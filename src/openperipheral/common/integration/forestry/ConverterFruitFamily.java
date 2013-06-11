package openperipheral.common.integration.forestry;

import java.util.HashMap;

import openperipheral.api.ITypeConverter;
import forestry.api.genetics.IFruitFamily;

public class ConverterFruitFamily implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class required) {
		return null;
	}

	@Override
	public Object toLua(Object o) {

		if (o instanceof IFruitFamily) {
			IFruitFamily f = (IFruitFamily) o;
			HashMap map = new HashMap();
			map.put("name", f.getName());
			map.put("scientific", f.getScientific());
			map.put("description", f.getDescription());
			return map;
		}

		return null;
	}

}

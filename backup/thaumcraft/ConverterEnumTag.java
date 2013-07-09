package openperipheral.core.integration.thaumcraft;

import java.util.HashMap;
import java.util.Map;

import openperipheral.api.ITypeConverter;
import thaumcraft.api.EnumTag;

public class ConverterEnumTag implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class required) {
		if (required == EnumTag.class) {
			if (o instanceof Map) {
				if (((Map) o).containsKey("id")) {
					o = ((Map) o).get("id");
				}
			}
			if (o instanceof Double) {
				return EnumTag.get(new Double((Double) o).intValue());
			}
		}
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o instanceof EnumTag) {
			EnumTag tag = (EnumTag) o;
			HashMap map = new HashMap();
			map.put("id", tag.id);
			map.put("name", tag.name);
			return map;
		}
		return null;
	}

}

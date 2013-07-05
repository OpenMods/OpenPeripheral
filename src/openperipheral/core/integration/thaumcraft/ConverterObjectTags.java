package openperipheral.core.integration.thaumcraft;

import java.util.HashMap;

import openperipheral.api.ITypeConverter;
import openperipheral.core.converter.TypeConversionRegistry;
import thaumcraft.api.EnumTag;
import thaumcraft.api.ObjectTags;

public class ConverterObjectTags implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class required) {
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o instanceof ObjectTags) {
			HashMap map = new HashMap();
			ObjectTags t = (ObjectTags) o;
			EnumTag[] aspects = t.getAspects();
			int j = 1;
			for (int i = 0; i < aspects.length; i++) {
				HashMap sub = new HashMap();
				EnumTag aspect = aspects[i];
				sub.put("amount", t.getAmount(aspect));
				sub.put("aspect", TypeConversionRegistry.toLua(aspect));
				map.put(j++, sub);
			}
			return map;
		}
		return null;
	}

}

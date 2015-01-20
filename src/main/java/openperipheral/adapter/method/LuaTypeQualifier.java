package openperipheral.adapter.method;

import java.util.Collection;
import java.util.List;

import openmods.reflection.TypeUtils;
import openperipheral.api.LuaArgType;

import com.google.common.collect.Lists;

public class LuaTypeQualifier {

	public interface Qualifier {
		public LuaArgType qualify(Class<?> cls);
	}

	private static List<Qualifier> qualifiers = Lists.newArrayList();

	public static void registerType(Qualifier qualifier) {
		qualifiers.add(qualifier);
	}

	public static void registerType(final Class<?> cls, final LuaArgType type) {
		qualifiers.add(new Qualifier() {
			@Override
			public LuaArgType qualify(Class<?> match) {
				return (cls.isAssignableFrom(match))? type : null;
			}
		});
	}

	public static LuaArgType qualifyArgType(Class<?> cls) {
		cls = TypeUtils.toObjectType(cls);

		if (cls == String.class) return LuaArgType.STRING;
		if (cls == Boolean.class) return LuaArgType.BOOLEAN;
		if (cls == Void.class) return LuaArgType.VOID;
		if (Number.class.isAssignableFrom(cls)) return LuaArgType.NUMBER;
		if (Collection.class.isAssignableFrom(cls)) return LuaArgType.TABLE;
		if (cls.isArray()) return LuaArgType.TABLE;
		if (cls.isEnum()) return LuaArgType.STRING;

		for (Qualifier q : qualifiers) {
			LuaArgType type = q.qualify(cls);
			if (type != null) return type;
		}

		throw new IllegalArgumentException(String.format("Can't categorize type '%s'", cls));
	}
}

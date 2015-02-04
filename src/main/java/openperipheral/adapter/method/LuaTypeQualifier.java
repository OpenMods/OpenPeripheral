package openperipheral.adapter.method;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import openmods.reflection.TypeUtils;
import openperipheral.api.adapter.method.ArgType;

import com.google.common.collect.Lists;

public class LuaTypeQualifier {

	public interface Qualifier {
		public ArgType qualify(Class<?> cls);
	}

	private static List<Qualifier> qualifiers = Lists.newArrayList();

	public static void registerType(Qualifier qualifier) {
		qualifiers.add(qualifier);
	}

	public static void registerType(final Class<?> cls, final ArgType type) {
		qualifiers.add(new Qualifier() {
			@Override
			public ArgType qualify(Class<?> match) {
				return (cls.isAssignableFrom(match))? type : null;
			}
		});
	}

	public static ArgType qualifyArgType(Class<?> cls) {
		cls = TypeUtils.toObjectType(cls);

		if (cls == String.class) return ArgType.STRING;
		if (cls == UUID.class) return ArgType.STRING;
		if (cls == Boolean.class) return ArgType.BOOLEAN;
		if (cls == Void.class) return ArgType.VOID;
		if (Number.class.isAssignableFrom(cls)) return ArgType.NUMBER;
		if (Collection.class.isAssignableFrom(cls)) return ArgType.TABLE;
		if (cls.isArray()) return ArgType.TABLE;
		if (cls.isEnum()) return ArgType.STRING;

		for (Qualifier q : qualifiers) {
			ArgType type = q.qualify(cls);
			if (type != null) return type;
		}

		throw new IllegalArgumentException(String.format("Can't categorize type '%s'", cls));
	}
}

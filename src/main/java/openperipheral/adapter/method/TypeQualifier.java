package openperipheral.adapter.method;

import java.util.*;

import openmods.reflection.TypeUtils;
import openperipheral.adapter.types.EnumeratedRange;
import openperipheral.adapter.types.IType;
import openperipheral.adapter.types.TypeHelper;

import com.google.common.collect.Lists;

public class TypeQualifier {

	public interface Qualifier {
		public IType qualify(Class<?> cls);
	}

	private static List<Qualifier> qualifiers = Lists.newArrayList();

	public static void registerType(Qualifier qualifier) {
		qualifiers.add(qualifier);
	}

	public static void registerType(final Class<?> cls, final IType type) {
		qualifiers.add(new Qualifier() {
			@Override
			public IType qualify(Class<?> match) {
				return (cls.isAssignableFrom(match))? type : null;
			}
		});
	}

	public static IType qualifyArgType(Class<?> cls) {
		cls = TypeUtils.toObjectType(cls);

		for (Qualifier q : qualifiers) {
			IType type = q.qualify(cls);
			if (type != null) return type;
		}

		if (cls == String.class) return TypeHelper.ARG_STRING;
		if (cls == UUID.class) return TypeHelper.ARG_STRING;
		if (cls == Boolean.class) return TypeHelper.ARG_BOOLEAN;
		if (cls == Void.class) return TypeHelper.ARG_VOID;
		if (Number.class.isAssignableFrom(cls)) return TypeHelper.ARG_NUMBER;
		if (Collection.class.isAssignableFrom(cls)) return TypeHelper.ARG_TABLE;
		if (Map.class.isAssignableFrom(cls)) return TypeHelper.ARG_TABLE;
		if (cls.isArray()) return TypeHelper.ARG_TABLE;
		if (cls.isEnum()) return TypeHelper.bounded(TypeHelper.ARG_STRING, EnumeratedRange.create(cls.getEnumConstants()));

		throw new IllegalArgumentException(String.format("Can't categorize type '%s'", cls));
	}
}

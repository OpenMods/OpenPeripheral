package openperipheral.adapter.method;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

import openmods.reflection.TypeUtils;
import openperipheral.adapter.types.*;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public class TypeQualifier {

	public static final TypeQualifier instance = new TypeQualifier();

	public interface Qualifier {
		public IType qualify(Class<?> cls);
	}

	private List<Qualifier> qualifiers = Lists.newArrayList();

	public void registerType(Qualifier qualifier) {
		qualifiers.add(qualifier);
	}

	public void registerType(final Class<?> cls, final IType type) {
		qualifiers.add(new Qualifier() {
			@Override
			public IType qualify(Class<?> match) {
				return (cls.isAssignableFrom(match))? type : null;
			}
		});
	}

	public IType qualifyType(Type type) {
		final TypeToken<?> typeToken = TypeToken.of(type);
		return qualifyType(typeToken);
	}

	private IType qualifyType(TypeToken<?> typeToken) {
		if (typeToken.isArray()) return qualifyArrayType(typeToken);
		else if (TypeUtils.MAP_TOKEN.isAssignableFrom(typeToken)) return qualifyMapType(typeToken);
		else if (TypeUtils.SET_TOKEN.isAssignableFrom(typeToken)) return qualifySetType(typeToken);
		else if (TypeUtils.COLLECTION_TOKEN.isAssignableFrom(typeToken)) return qualifyCollectionType(typeToken);

		Class<?> cls = TypeUtils.toObjectType(typeToken.getRawType());

		for (Qualifier q : qualifiers) {
			IType result = q.qualify(cls);
			if (result != null) return result;
		}

		if (cls == String.class) return TypeHelper.ARG_STRING;
		if (cls == UUID.class) return TypeHelper.ARG_STRING;
		if (cls == Boolean.class) return TypeHelper.ARG_BOOLEAN;
		if (cls == Void.class) return TypeHelper.ARG_VOID;
		if (Number.class.isAssignableFrom(cls)) return TypeHelper.ARG_NUMBER;
		if (cls.isEnum()) return TypeHelper.bounded(TypeHelper.ARG_STRING, EnumeratedRange.create(cls.getEnumConstants()));

		throw new IllegalArgumentException(String.format("Can't categorize type '%s'", cls));
	}

	protected IType createListType(final TypeToken<?> type) {
		return (type.getRawType() != Object.class)
				? new ListType(qualifyType(type))
				: TypeHelper.ARG_TABLE;
	}

	protected IType createSetType(final TypeToken<?> type) {
		return (type.getRawType() != Object.class)
				? new SetType(qualifyType(type))
				: TypeHelper.ARG_TABLE;
	}

	private IType qualifyArrayType(TypeToken<?> typeToken) {
		final TypeToken<?> componentType = typeToken.getComponentType();
		return createListType(componentType);
	}

	private IType qualifyCollectionType(TypeToken<?> typeToken) {
		final TypeToken<?> componentType = typeToken.resolveType(TypeUtils.COLLECTION_VALUE_PARAM);
		return createListType(componentType);
	}

	private IType qualifySetType(TypeToken<?> typeToken) {
		final TypeToken<?> componentType = typeToken.resolveType(TypeUtils.SET_VALUE_PARAM);
		return createSetType(componentType);
	}

	private IType qualifyMapType(TypeToken<?> typeToken) {
		final TypeToken<?> keyType = typeToken.resolveType(TypeUtils.MAP_KEY_PARAM);
		final TypeToken<?> valueType = typeToken.resolveType(TypeUtils.MAP_VALUE_PARAM);

		if (keyType.getRawType() == Object.class || valueType.getRawType() == Object.class) return TypeHelper.ARG_TABLE;

		final IType qualifiedKeyType = qualifyType(keyType);
		final IType qualifiedValueType = qualifyType(valueType);

		return new MapType(qualifiedKeyType, qualifiedValueType);
	}
}

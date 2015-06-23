package openperipheral.adapter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

import openmods.reflection.TypeUtils;
import openperipheral.adapter.types.*;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.adapter.ITypeQualifier;
import openperipheral.converter.StructCache;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public class TypeQualifier implements ITypeQualifier {

	private static class ClassQualifierAdapter implements IGenericQualifier {
		private final IClassQualifier wrapped;

		public ClassQualifierAdapter(IClassQualifier wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public IScriptType qualify(Type type) {
			final TypeToken<?> token = TypeToken.of(type);
			return wrapped.qualify(token.getRawType());
		}

	}

	public static final TypeQualifier INSTANCE = new TypeQualifier();

	private final List<IGenericQualifier> qualifiers = Lists.newArrayList();

	@Override
	public void registerQualifier(IGenericQualifier qualifier) {
		qualifiers.add(qualifier);
	}

	@Override
	public void registerQualifier(IClassQualifier qualifier) {
		qualifiers.add(new ClassQualifierAdapter(qualifier));
	}

	@Override
	public void registerType(final Class<?> cls, final IScriptType type) {
		final TypeToken<?> match = TypeToken.of(cls);
		qualifiers.add(new IGenericQualifier() {
			@Override
			public IScriptType qualify(Type t) {
				return (match.isAssignableFrom(t))? type : null;
			}
		});
	}

	@Override
	public IScriptType qualifyType(Type type) {
		final TypeToken<?> typeToken = TypeToken.of(type);
		return qualifyType(typeToken);
	}

	private IScriptType qualifyType(TypeToken<?> typeToken) {
		if (typeToken.isArray()) return qualifyArrayType(typeToken);
		else if (TypeUtils.MAP_TOKEN.isAssignableFrom(typeToken)) return qualifyMapType(typeToken);
		else if (TypeUtils.SET_TOKEN.isAssignableFrom(typeToken)) return qualifySetType(typeToken);
		else if (TypeUtils.COLLECTION_TOKEN.isAssignableFrom(typeToken)) return qualifyCollectionType(typeToken);

		Class<?> cls = TypeUtils.toObjectType(typeToken.getRawType());

		for (IGenericQualifier q : qualifiers) {
			IScriptType result = q.qualify(cls);
			if (result != null) return result;
		}

		if (cls == String.class) return SingleArgType.STRING;
		if (cls == UUID.class) return SingleArgType.STRING;
		if (cls == Boolean.class) return SingleArgType.BOOLEAN;
		if (cls == Void.class) return SingleArgType.VOID;
		if (Number.class.isAssignableFrom(cls)) return SingleArgType.NUMBER;
		if (cls.isEnum()) return TypeHelper.bounded(SingleArgType.STRING, EnumeratedRange.create(cls.getEnumConstants()));
		if (StructCache.instance.isStruct(cls)) return SingleArgType.TABLE;

		throw new IllegalArgumentException(String.format("Can't categorize type '%s'", cls));
	}

	protected IScriptType createListType(final TypeToken<?> type) {
		return (type.getRawType() != Object.class)
				? new ListType(qualifyType(type))
				: SingleArgType.TABLE;
	}

	protected IScriptType createSetType(final TypeToken<?> type) {
		return (type.getRawType() != Object.class)
				? new SetType(qualifyType(type))
				: SingleArgType.TABLE;
	}

	private IScriptType qualifyArrayType(TypeToken<?> typeToken) {
		final TypeToken<?> componentType = typeToken.getComponentType();
		return createListType(componentType);
	}

	private IScriptType qualifyCollectionType(TypeToken<?> typeToken) {
		final TypeToken<?> componentType = typeToken.resolveType(TypeUtils.COLLECTION_VALUE_PARAM);
		return createListType(componentType);
	}

	private IScriptType qualifySetType(TypeToken<?> typeToken) {
		final TypeToken<?> componentType = typeToken.resolveType(TypeUtils.SET_VALUE_PARAM);
		return createSetType(componentType);
	}

	private IScriptType qualifyMapType(TypeToken<?> typeToken) {
		final TypeToken<?> keyType = typeToken.resolveType(TypeUtils.MAP_KEY_PARAM);
		final TypeToken<?> valueType = typeToken.resolveType(TypeUtils.MAP_VALUE_PARAM);

		if (keyType.getRawType() == Object.class || valueType.getRawType() == Object.class) return SingleArgType.TABLE;

		final IScriptType qualifiedKeyType = qualifyType(keyType);
		final IScriptType qualifiedValueType = qualifyType(valueType);

		return new MapType(qualifiedKeyType, qualifiedValueType);
	}
}

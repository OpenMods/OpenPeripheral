package openperipheral.adapter.property;

import java.lang.reflect.Type;
import java.util.Set;

import openmods.reflection.TypeUtils;
import openperipheral.adapter.method.TypeQualifier;
import openperipheral.adapter.types.IType;
import openperipheral.api.helpers.Index;
import openperipheral.converter.StructCache;
import openperipheral.converter.StructCache.IFieldHandler;
import openperipheral.converter.StructCache.IStructHandler;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

public class FieldTypeInfoBuilder {

	private final TypeToken<?> fieldType;

	public FieldTypeInfoBuilder(Type fieldType) {
		this.fieldType = TypeToken.of(fieldType);
	}

	private static IValueTypeProvider createConstantTypeProvider(final Type type) {
		return new IValueTypeProvider() {
			@Override
			public Type getType(Object key) {
				return type;
			}
		};
	}

	private static IValueTypeProvider createStructTypeProvider(final IStructHandler handler) {
		return new IValueTypeProvider() {
			@Override
			public Type getType(Object key) {
				final String fieldName = key.toString();
				final IFieldHandler field = handler.field(fieldName);
				Preconditions.checkNotNull(field, "Failed to found field '%s'", fieldName);
				return field.type();
			}
		};
	}

	private static IType identityCommonStructType(IStructHandler handler) {
		Set<Type> types = Sets.newHashSet();

		for (String fieldName : handler.fields()) {
			IFieldHandler fieldHandler = handler.field(fieldName);
			types.add(fieldHandler.type());
		}

		if (types.size() == 1) {
			Type type = types.iterator().next();
			return TypeQualifier.instance.qualifyType(type);
		} else return IType.WILDCHAR;
	}

	private Pair<Type, Type> identifyBasicTypes() {
		final Type key;
		final Type value;
		if (TypeUtils.MAP_TOKEN.isAssignableFrom(fieldType)) {
			key = fieldType.resolveType(TypeUtils.MAP_KEY_PARAM).getType();
			value = fieldType.resolveType(TypeUtils.MAP_VALUE_PARAM).getType();
		} else if (TypeUtils.LIST_TOKEN.isAssignableFrom(fieldType)) {
			key = Index.class;
			value = fieldType.resolveType(TypeUtils.LIST_VALUE_PARAM).getType();
		} else if (fieldType.isArray()) {
			key = Index.class;
			value = fieldType.getComponentType().getType();
		} else throw new IllegalArgumentException("Failed to deduce value type from" + fieldType);

		return Pair.of(key, value);
	}

	private Type overridenKeyType;

	private IType overridenKeyDocType;

	private Type overridenValueType;

	private IType overridenValueDocType;

	public static class Result {
		public final Type keyType;

		public final IType keyDocType;

		public final IValueTypeProvider valueType;

		public final IType valueDocType;

		public Result(Type keyType, IType keyDocType, IValueTypeProvider valueType, IType valueDocType) {
			this.keyType = keyType;
			this.keyDocType = keyDocType;
			this.valueType = valueType;
			this.valueDocType = valueDocType;
		}
	}

	public void overrideKeyType(Class<?> type) {
		this.overridenKeyType = type;
	}

	public void overrideKeyDocType(IType type) {
		this.overridenKeyDocType = type;
	}

	public void overrideValueType(Class<?> type) {
		this.overridenValueType = type;
	}

	public void overrideValueDocType(IType type) {
		this.overridenValueDocType = type;
	}

	public Result build() {
		Type keyType = overridenKeyType;
		IType keyDocType = overridenKeyDocType;
		IType valueDocType = overridenValueDocType;

		IValueTypeProvider valueTypeProvider = null;

		if (overridenValueType != null) {
			valueTypeProvider = createConstantTypeProvider(overridenValueType);
			if (valueDocType == null) valueDocType = TypeQualifier.instance.qualifyType(overridenValueType);
		}

		if (keyType == null || overridenValueType == null) {
			if (StructCache.instance.isStruct(fieldType.getRawType())) {
				final IStructHandler handler = StructCache.instance.getHandler(fieldType.getRawType());
				if (keyType == null) keyType = String.class;
				if (valueTypeProvider == null) valueTypeProvider = createStructTypeProvider(handler);
				if (valueDocType == null) valueDocType = identityCommonStructType(handler);
			} else {
				final Pair<Type, Type> deducedTypes = identifyBasicTypes();
				if (keyType == null) keyType = deducedTypes.getKey();
				final Type valueType = deducedTypes.getValue();
				if (valueTypeProvider == null) valueTypeProvider = createConstantTypeProvider(valueType);
				if (valueDocType == null) valueDocType = TypeQualifier.instance.qualifyType(valueType);
			}
		}

		if (keyDocType == null) keyDocType = TypeQualifier.instance.qualifyType(keyType);

		Preconditions.checkNotNull(keyType, "Failed to deduce key type");
		Preconditions.checkNotNull(keyDocType, "Failed to deduce key doc type");
		Preconditions.checkNotNull(valueTypeProvider, "Failed to deduce value type");
		Preconditions.checkNotNull(valueDocType, "Failed to deduce value doc type");

		return new Result(keyType, keyDocType, valueTypeProvider, valueDocType);
	}
}

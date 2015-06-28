package openperipheral.adapter.property;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Set;

import openmods.reflection.TypeUtils;
import openperipheral.adapter.types.SingleArgType;
import openperipheral.adapter.types.SingleType;
import openperipheral.adapter.types.classifier.TypeClassifier;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.helpers.Index;
import openperipheral.api.property.*;
import openperipheral.converter.StructHandlerProvider;
import openperipheral.converter.StructHandlerProvider.IFieldHandler;
import openperipheral.converter.StructHandlerProvider.IStructHandler;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

public class IndexedTypeInfoBuilder {
	private static final TypeToken<?> CUSTOM_PROPERTY_TYPE = TypeToken.of(IIndexedCustomProperty.class);
	private static final TypeVariable<?> CUSTOM_PROPERTY_KEY_TYPE;
	private static final TypeVariable<?> CUSTOM_PROPERTY_VALUE_TYPE;

	private static final TypeToken<?> CUSTOM_TYPED_PROPERTY_TYPE = TypeToken.of(IIndexedTypedCustomProperty.class);
	private static final TypeVariable<?> CUSTOM_TYPED_PROPERTY_KEY_TYPE;
	private static final TypeVariable<?> CUSTOM_TYPED_PROPERTY_VALUE_TYPE;

	static {
		{
			TypeVariable<?>[] vars = IIndexedCustomProperty.class.getTypeParameters();
			CUSTOM_PROPERTY_KEY_TYPE = vars[0];
			CUSTOM_PROPERTY_VALUE_TYPE = vars[1];
		}

		{
			TypeVariable<?>[] vars = IIndexedTypedCustomProperty.class.getTypeParameters();
			CUSTOM_TYPED_PROPERTY_KEY_TYPE = vars[0];
			CUSTOM_TYPED_PROPERTY_VALUE_TYPE = vars[1];
		}
	}

	private final TypeToken<?> fieldType;

	public IndexedTypeInfoBuilder(Type fieldType) {
		this.fieldType = TypeToken.of(fieldType);
	}

	private interface ITypeProvider {
		public Type getType(Object target, Object key);
	}

	private static interface ITypesProvider {
		public Type getKeyType();

		public IScriptType getKeyDocType();

		public ITypeProvider getValueType();

		public IScriptType getValueDocType();
	}

	private static class ConstantTypesProvider implements ITypesProvider {

		private final Type keyType;
		private final Type valueType;

		public ConstantTypesProvider(Type keyType, Type valueType) {
			this.keyType = keyType;
			this.valueType = valueType;
		}

		@Override
		public Type getKeyType() {
			return keyType;
		}

		@Override
		public IScriptType getKeyDocType() {
			return classifyType(keyType);
		}

		@Override
		public ITypeProvider getValueType() {
			return createConstantTypeProvider(valueType);
		}

		@Override
		public IScriptType getValueDocType() {
			return classifyType(valueType);
		}
	}

	private static IScriptType classifyType(final Type type) {
		return TypeClassifier.INSTANCE.classifyType(type);
	}

	private static Type getTypeVariable(TypeToken<?> type, TypeVariable<?> var) {
		return type.resolveType(var).getType();
	}

	private static class MapTypesProvider extends ConstantTypesProvider {
		public MapTypesProvider(TypeToken<?> type) {
			super(getTypeVariable(type, TypeUtils.MAP_KEY_PARAM), getTypeVariable(type, TypeUtils.MAP_VALUE_PARAM));
		}
	}

	private static class ListTypesProvider extends ConstantTypesProvider {
		public ListTypesProvider(TypeToken<?> type) {
			super(Index.class, getTypeVariable(type, TypeUtils.LIST_VALUE_PARAM));
		}
	}

	private static class ArrayTypesProvider extends ConstantTypesProvider {
		public ArrayTypesProvider(TypeToken<?> type) {
			super(Index.class, type.getComponentType().getType());
		}
	}

	private static class StructTypesProvider implements ITypesProvider {

		private final IStructHandler handler;

		public StructTypesProvider(Class<?> structCls) {
			this.handler = StructHandlerProvider.instance.getHandler(structCls);
		}

		private static IScriptType identityCommonStructType(IStructHandler handler) {
			Set<Type> types = Sets.newHashSet();

			for (String fieldName : handler.fields()) {
				IFieldHandler fieldHandler = handler.field(fieldName);
				types.add(fieldHandler.type());
			}

			if (types.size() == 1) {
				Type type = types.iterator().next();
				return classifyType(type);
			} else return SingleType.WILDCHAR;
		}

		private static ITypeProvider createStructTypeProvider(final IStructHandler handler) {
			return new ITypeProvider() {
				@Override
				public Type getType(Object target, Object key) {
					final String fieldName = key.toString();
					final IFieldHandler field = handler.field(fieldName);
					Preconditions.checkNotNull(field, "Failed to found field '%s'", fieldName);
					return field.type();
				}
			};
		}

		@Override
		public Type getKeyType() {
			return String.class;
		}

		@Override
		public IScriptType getKeyDocType() {
			return SingleArgType.STRING;
		}

		@Override
		public ITypeProvider getValueType() {
			return createStructTypeProvider(handler);
		}

		@Override
		public IScriptType getValueDocType() {
			return identityCommonStructType(handler);
		}
	}

	private static final ITypeProvider DELEGATING_TYPE_PROVIDER = new ITypeProvider() {
		@Override
		public Type getType(Object target, Object key) {
			return ((IIndexedTypedCustomProperty<?, ?>)target).getType(key);
		}
	};

	private abstract static class CustomPropertyTypesProviderBase implements ITypesProvider {
		private final Class<?> fieldType;
		private final Type keyType;
		protected final Type valueType;

		public CustomPropertyTypesProviderBase(TypeToken<?> fieldType, TypeVariable<?> keyVar, TypeVariable<?> valueVar) {
			this.fieldType = fieldType.getRawType();
			this.keyType = fieldType.resolveType(keyVar).getType();
			this.valueType = fieldType.resolveType(valueVar).getType();
		}

		@Override
		public Type getKeyType() {
			return keyType;
		}

		@Override
		public IScriptType getKeyDocType() {
			final PropertyKeyDocType customKeyDoc = fieldType.getAnnotation(PropertyKeyDocType.class);
			return (customKeyDoc == null)? classifyType(keyType) : SingleArgType.valueOf(customKeyDoc.value());
		}

		@Override
		public IScriptType getValueDocType() {
			final PropertyValueDocType customValueDoc = fieldType.getAnnotation(PropertyValueDocType.class);
			return (customValueDoc == null)? classifyType(valueType) : SingleArgType.valueOf(customValueDoc.value());
		}
	}

	private static class CustomPropertyTypesProvider extends CustomPropertyTypesProviderBase {

		public CustomPropertyTypesProvider(TypeToken<?> fieldType) {
			super(fieldType, CUSTOM_PROPERTY_KEY_TYPE, CUSTOM_PROPERTY_VALUE_TYPE);
		}

		@Override
		public ITypeProvider getValueType() {
			return createConstantTypeProvider(valueType);
		}
	}

	private static class CustomTypedPropertyTypesProvider extends CustomPropertyTypesProviderBase {

		public CustomTypedPropertyTypesProvider(TypeToken<?> fieldType) {
			super(fieldType, CUSTOM_TYPED_PROPERTY_KEY_TYPE, CUSTOM_TYPED_PROPERTY_VALUE_TYPE);
		}

		@Override
		public ITypeProvider getValueType() {
			return DELEGATING_TYPE_PROVIDER;
		}
	}

	private static ITypeProvider createConstantTypeProvider(final Type type) {
		return new ITypeProvider() {
			@Override
			public Type getType(Object target, Object key) {
				return type;
			}
		};
	}

	private ITypesProvider typesProvider;

	private Type overridenKeyType;

	private IScriptType overridenKeyDocType;

	private Type overridenValueType;

	private IScriptType overridenValueDocType;

	public void overrideKeyType(Class<?> type) {
		this.overridenKeyType = type;
	}

	public void overrideKeyDocType(IScriptType type) {
		this.overridenKeyDocType = type;
	}

	public void overrideValueType(Class<?> type) {
		this.overridenValueType = type;
	}

	public void overrideValueDocType(IScriptType type) {
		this.overridenValueDocType = type;
	}

	private static ITypesProvider createTypesProvider(TypeToken<?> fieldType) {
		if (CUSTOM_PROPERTY_TYPE.isAssignableFrom(fieldType)) {
			if (CUSTOM_TYPED_PROPERTY_TYPE.isAssignableFrom(fieldType)) return new CustomTypedPropertyTypesProvider(fieldType);
			else return new CustomPropertyTypesProvider(fieldType);
		}

		if (TypeUtils.MAP_TOKEN.isAssignableFrom(fieldType)) return new MapTypesProvider(fieldType);
		if (TypeUtils.LIST_TOKEN.isAssignableFrom(fieldType)) return new ListTypesProvider(fieldType);
		if (fieldType.isArray()) return new ArrayTypesProvider(fieldType);
		final Class<?> rawType = fieldType.getRawType();

		if (StructHandlerProvider.instance.isStruct(rawType)) return new StructTypesProvider(rawType);

		throw new IllegalArgumentException("Failed to deduce value type from" + fieldType);
	}

	private ITypesProvider getTypesProvider() {
		if (typesProvider == null) typesProvider = createTypesProvider(fieldType);
		return typesProvider;
	}

	private static IndexedTypeInfo createTypeInfo(final Type keyType, final IScriptType keyDocType, final ITypeProvider valueTypeProvider, final IScriptType valueDocType) {
		Preconditions.checkNotNull(keyType, "Failed to deduce key type");
		Preconditions.checkNotNull(keyDocType, "Failed to deduce key doc type");
		Preconditions.checkNotNull(valueTypeProvider, "Failed to deduce value type");
		Preconditions.checkNotNull(valueDocType, "Failed to deduce value doc type");

		return new IndexedTypeInfo(keyType, keyDocType, valueDocType) {
			@Override
			public Type getValueType(Object target, Object key) {
				return valueTypeProvider.getType(target, key);
			}
		};
	}

	public IndexedTypeInfo build() {
		final Type keyType;

		if (overridenKeyType == null) {
			keyType = getTypesProvider().getKeyType();
		} else {
			keyType = overridenKeyType;
		}

		final IScriptType keyDocType;

		if (overridenKeyDocType == null) {
			if (overridenKeyType == null) {
				keyDocType = getTypesProvider().getKeyDocType();
			} else {
				keyDocType = classifyType(overridenKeyType);
			}
		} else {
			keyDocType = overridenKeyDocType;
		}

		final ITypeProvider valueTypeProvider;

		if (overridenValueType == null) {
			valueTypeProvider = getTypesProvider().getValueType();
		} else {
			valueTypeProvider = createConstantTypeProvider(overridenValueType);
		}

		final IScriptType valueDocType;

		if (overridenValueDocType == null) {
			if (overridenValueType == null) {
				valueDocType = getTypesProvider().getValueDocType();
			} else {
				valueDocType = classifyType(overridenValueType);
			}
		} else {
			valueDocType = overridenValueDocType;
		}

		return createTypeInfo(keyType, keyDocType, valueTypeProvider, valueDocType);
	}
}

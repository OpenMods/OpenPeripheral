package openperipheral.adapter.property;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import openperipheral.adapter.TypeQualifier;
import openperipheral.adapter.types.SingleArgType;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.property.ISingleCustomProperty;
import openperipheral.api.property.ISingleTypedCustomProperty;
import openperipheral.api.property.PropertyValueDocType;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

public class SingleTypeInfoBuilder {
	private static final TypeToken<?> CUSTOM_PROPERTY_TYPE = TypeToken.of(ISingleCustomProperty.class);
	private static final TypeVariable<?> CUSTOM_PROPERTY_VALUE_TYPE;

	private static final TypeToken<?> CUSTOM_TYPED_PROPERTY_TYPE = TypeToken.of(ISingleTypedCustomProperty.class);
	private static final TypeVariable<?> CUSTOM_TYPED_PROPERTY_VALUE_TYPE;

	static {
		{
			TypeVariable<?>[] vars = ISingleCustomProperty.class.getTypeParameters();
			CUSTOM_PROPERTY_VALUE_TYPE = vars[0];
		}

		{
			TypeVariable<?>[] vars = ISingleTypedCustomProperty.class.getTypeParameters();
			CUSTOM_TYPED_PROPERTY_VALUE_TYPE = vars[0];
		}
	}

	private interface ITypeProvider {
		public Type getType(Object target);
	}

	private static ITypeProvider createConstantTypeProvider(final Type type) {
		return new ITypeProvider() {
			@Override
			public Type getType(Object target) {
				return type;
			}
		};
	}

	private static interface ITypesProvider {
		public ITypeProvider getValueType();

		public IScriptType getValueDocType();
	}

	private static final ITypeProvider DELEGATING_TYPE_PROVIDER = new ITypeProvider() {
		@Override
		public Type getType(Object target) {
			return ((ISingleTypedCustomProperty<?>)target).getType();
		}
	};

	private abstract static class CustomPropertyProviderBase implements ITypesProvider {
		private final Class<?> fieldType;
		protected final Type valueType;

		public CustomPropertyProviderBase(TypeToken<?> fieldType, TypeVariable<?> var) {
			this.fieldType = fieldType.getRawType();
			this.valueType = fieldType.resolveType(var).getType();
		}

		@Override
		public IScriptType getValueDocType() {
			final PropertyValueDocType customValueDoc = fieldType.getAnnotation(PropertyValueDocType.class);
			return (customValueDoc == null)? TypeQualifier.INSTANCE.qualifyType(valueType) : SingleArgType.valueOf(customValueDoc.value());
		}
	}

	private static class CustomPropertyTypesProvider extends CustomPropertyProviderBase {
		public CustomPropertyTypesProvider(TypeToken<?> fieldType) {
			super(fieldType, CUSTOM_PROPERTY_VALUE_TYPE);
		}

		@Override
		public ITypeProvider getValueType() {
			return createConstantTypeProvider(valueType);
		}
	}

	private static class CustomTypedPropertyTypesProvider extends CustomPropertyProviderBase {
		public CustomTypedPropertyTypesProvider(TypeToken<?> fieldType) {
			super(fieldType, CUSTOM_TYPED_PROPERTY_VALUE_TYPE);
		}

		@Override
		public ITypeProvider getValueType() {
			return DELEGATING_TYPE_PROVIDER;
		}
	}

	private static class DefaultPropertyTypesProvider implements ITypesProvider {

		private final Type fieldType;

		public DefaultPropertyTypesProvider(Type fieldType) {
			this.fieldType = fieldType;
		}

		@Override
		public ITypeProvider getValueType() {
			return createConstantTypeProvider(fieldType);
		}

		@Override
		public IScriptType getValueDocType() {
			return TypeQualifier.INSTANCE.qualifyType(fieldType);
		}

	}

	private final TypeToken<?> fieldType;

	private Type overridenValueType;

	private IScriptType overridenValueDocType;
	private ITypesProvider typesProvider;

	public SingleTypeInfoBuilder(Type fieldType) {
		this.fieldType = TypeToken.of(fieldType);
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
		return new DefaultPropertyTypesProvider(fieldType.getType());
	}

	private ITypesProvider getTypesProvider() {
		if (typesProvider == null) typesProvider = createTypesProvider(fieldType);
		return typesProvider;
	}

	private static SingleTypeInfo createTypeInfo(final ITypeProvider valueTypeProvider, final IScriptType valueDocType) {
		Preconditions.checkNotNull(valueTypeProvider, "Failed to deduce value type");
		Preconditions.checkNotNull(valueDocType, "Failed to deduce value doc type");

		return new SingleTypeInfo(valueDocType) {
			@Override
			public Type getValueType(Object target) {
				return valueTypeProvider.getType(target);
			}
		};
	}

	public SingleTypeInfo build() {
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
				valueDocType = TypeQualifier.INSTANCE.qualifyType(overridenValueType);
			}
		} else {
			valueDocType = overridenValueDocType;
		}

		return createTypeInfo(valueTypeProvider, valueDocType);
	}
}

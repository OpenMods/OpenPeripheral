package openperipheral.adapter.property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import openmods.reflection.TypeUtils;
import openperipheral.adapter.IMethodDescription;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.method.LuaTypeQualifier;
import openperipheral.api.adapter.*;
import openperipheral.api.adapter.IndexedCallbackProperty.GetFromFieldType;
import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.helpers.Index;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;

public class PropertyListBuilder {

	private static IValueTypeProvider createConstantTypeProvider(final Type type) {
		return new IValueTypeProvider() {
			@Override
			public Type getType(Object key) {
				return type;
			}
		};
	}

	private static Type getIndexType(Type genericType) {
		final TypeToken<?> type = TypeToken.of(genericType);
		final Class<?> rawType = type.getRawType();

		if (Map.class.isAssignableFrom(rawType)) {
			return type.resolveType(TypeUtils.MAP_KEY_PARAM).getType();
		} else if (List.class.isAssignableFrom(rawType)) {
			return Index.class;
		} else if (rawType.isArray()) { return Index.class; }

		// TODO structs

		throw new IllegalArgumentException("Failed to deduce key type from " + genericType);
	}

	private static ArgType interpretDocType(ArgType givenType, Class<?> targetType) {
		return givenType == ArgType.AUTO? LuaTypeQualifier.qualifyArgType(targetType) : givenType;
	}

	private static ArgType interpretDocType(ArgType givenType, Type type) {
		final Class<?> rawType = TypeToken.of(type).getRawType();
		return givenType == ArgType.AUTO? LuaTypeQualifier.qualifyArgType(rawType) : givenType;
	}

	private static Type getBasicValueType(Type genericType) {
		final TypeToken<?> type = TypeToken.of(genericType);
		final Class<?> rawType = type.getRawType();

		if (Map.class.isAssignableFrom(rawType)) {
			return type.resolveType(TypeUtils.MAP_VALUE_PARAM).getType();
		} else if (List.class.isAssignableFrom(rawType)) {
			return type.resolveType(TypeUtils.LIST_VALUE_PARAM).getType();
		} else if (rawType.isArray()) {
			return rawType.getComponentType();
		} else return null;
	}

	private class Parameters {
		public final String name;
		public final String getterDescription;
		public final String setterDescription;
		public final boolean isDelegating;
		public final boolean readOnly;

		public Parameters(String name, String getterDescription, String setterDescription, boolean isDelegating, boolean readOnly) {
			this.name = Strings.isNullOrEmpty(name)? field.getName() : name;
			this.getterDescription = getterDescription;
			this.setterDescription = setterDescription;
			this.isDelegating = isDelegating;
			this.readOnly = readOnly;
		}
	}

	private class SingleParameters extends Parameters {

		public final ArgType valueType;

		public SingleParameters(String name, String getterDescription, String setterDescription, boolean isDelegating, boolean readOnly, ArgType valueType) {
			super(name, getterDescription, setterDescription, isDelegating, readOnly);
			this.valueType = interpretDocType(valueType, field.getType());
		}
	}

	private class IndexedParameters extends Parameters {
		public final boolean expandable;
		public final Type keyType;
		public final ArgType docKeyType;
		public final IValueTypeProvider valueTypeProvider;
		public final ArgType docValueType;

		public IndexedParameters(String name, String getterDescription, String setterDescription, boolean isDelegating, boolean readOnly, boolean expandable, Class<?> keyType, ArgType keyDocType, Class<?> valueType, ArgType valueDocType) {
			super(name, getterDescription, setterDescription, isDelegating, readOnly);
			this.expandable = expandable;

			this.keyType = keyType == GetFromFieldType.class? getIndexType(field.getGenericType()) : keyType;
			Class<?> rawIndexType = TypeToken.of(this.keyType).getRawType();
			this.docKeyType = interpretDocType(keyDocType, rawIndexType);

			Type basicReturnType = valueType == GetFromFieldType.class? getBasicValueType(field.getGenericType()) : valueType;

			// TODO structs
			if (basicReturnType == null) throw new IllegalArgumentException("Failed to find return type for field of type " + valueType);

			this.valueTypeProvider = createConstantTypeProvider(basicReturnType);
			this.docValueType = interpretDocType(valueDocType, basicReturnType);
		}
	}

	private final Field field;
	private final String source;
	private SingleParameters singleParameters;
	private IndexedParameters indexedParameters;

	public PropertyListBuilder(Field field, String source) {
		this.field = field;
		this.source = source;
	}

	public void addSingle(String name, String getterDescription, String setterDescription, boolean isDelegating, boolean readOnly, ArgType type) {
		this.singleParameters = new SingleParameters(name, getterDescription, setterDescription, isDelegating, readOnly, type);
	}

	public void addProperty(Property property) {
		addSingle(property.name(), property.getterDesc(), property.setterDesc(), false, property.readOnly(), property.type());
	}

	public void addProperty(CallbackProperty property) {
		Preconditions.checkArgument(IPropertyCallback.class.isAssignableFrom(field.getDeclaringClass()));
		addSingle(property.name(), property.getterDesc(), property.setterDesc(), true, property.readOnly(), property.type());
	}

	public void addIndexed(String name, String getterDescription, String setterDescription, boolean isDelegating, boolean readOnly, boolean expandable, Class<?> keyType, ArgType keyDocType, Class<?> valueType, ArgType valueDocType) {
		this.indexedParameters = new IndexedParameters(name, getterDescription, setterDescription, isDelegating, readOnly, expandable, keyType, keyDocType, valueType, valueDocType);
	}

	public void addProperty(IndexedProperty property) {
		addIndexed(property.name(), property.getterDesc(), property.setterDesc(), false, property.readOnly(), property.expandable(), GetFromFieldType.class, property.indexType(), GetFromFieldType.class, ArgType.AUTO);
	}

	public void addProperty(IndexedCallbackProperty property) {
		Preconditions.checkArgument(IIndexedPropertyCallback.class.isAssignableFrom(field.getDeclaringClass()));
		addIndexed(property.name(), property.getterDesc(), property.setterDesc(), true, property.readOnly(), property.expandable(), property.keyType(), property.keyDocType(), property.valueType(), property.valueDocType());
	}

	public PropertyListBuilder configureFromFieldProperties() {
		final Property singleProperty = field.getAnnotation(Property.class);
		final CallbackProperty singleCallbackProperty = field.getAnnotation(CallbackProperty.class);
		if (singleProperty != null) addProperty(singleProperty);
		else if (singleCallbackProperty != null) addProperty(singleCallbackProperty);

		final IndexedProperty indexedProperty = field.getAnnotation(IndexedProperty.class);
		final IndexedCallbackProperty indexedCallbackProperty = field.getAnnotation(IndexedCallbackProperty.class);
		if (indexedProperty != null) addProperty(indexedProperty);
		else if (indexedCallbackProperty != null) addProperty(indexedCallbackProperty);

		return this;
	}

	public void addMethods(List<IMethodExecutor> output) {
		field.setAccessible(true);

		if (singleParameters != null && indexedParameters == null) {
			addSinglePropertyMethods(output, singleParameters);
		} else if (singleParameters == null && indexedParameters != null) {
			addIndexedPropertyMethods(output, indexedParameters);
		} else if (singleParameters != null && indexedParameters != null) {
			if (singleParameters.name.equals(indexedParameters.name)) {
				addMergedPropertyMethods(output, singleParameters, indexedParameters);
			} else {
				addSinglePropertyMethods(output, singleParameters);
				addIndexedPropertyMethods(output, indexedParameters);
			}
		}
	}

	private void addSinglePropertyMethods(List<IMethodExecutor> output, SingleParameters params) {
		precheckSingleField(params);
		final IFieldManipulator fieldManipulator = FieldManipulatorProviders.getProvider(params.isDelegating);
		output.add(createSinglePropertyGetter(params, fieldManipulator));
		if (!params.readOnly) output.add(createSinglePropertySetter(params, fieldManipulator));
	}

	private void addIndexedPropertyMethods(List<IMethodExecutor> output, IndexedParameters params) {
		precheckIndexedField(params);
		final IIndexedFieldManipulator fieldManipulator = FieldManipulatorProviders.getIndexedProvider(field.getType(), params.isDelegating, params.expandable);
		output.add(createIndexedPropertyGetter(params, fieldManipulator));
		if (!params.readOnly) output.add(createIndexedPropertySetter(params, fieldManipulator));
	}

	private void addMergedPropertyMethods(List<IMethodExecutor> output, SingleParameters singleParameters, IndexedParameters indexedParameters) {
		precheckSingleField(singleParameters);
		precheckIndexedField(indexedParameters);

		final IFieldManipulator singleFieldManipulator = FieldManipulatorProviders.getProvider(singleParameters.isDelegating);
		final IIndexedFieldManipulator indexedFieldManipulator = FieldManipulatorProviders.getIndexedProvider(field.getType(), indexedParameters.isDelegating, indexedParameters.expandable);

		output.add(createMergedPropertyGetter(singleParameters, singleFieldManipulator, indexedParameters, indexedFieldManipulator));

		if (!singleParameters.readOnly && !indexedParameters.readOnly) {
			output.add(createMergedPropertySetter(singleParameters, singleFieldManipulator, indexedParameters, indexedFieldManipulator));
		} else if (!indexedParameters.readOnly) {
			output.add(createIndexedPropertySetter(indexedParameters, indexedFieldManipulator));
		} else if (!singleParameters.readOnly) {
			output.add(createSinglePropertySetter(singleParameters, singleFieldManipulator));
		}
	}

	private IMethodExecutor createSinglePropertyGetter(SingleParameters params, final IFieldManipulator fieldManipulator) {
		final PropertyDescriptionBuilder descriptionBuilder = new PropertyDescriptionBuilder(params.name, source);
		descriptionBuilder.addSingleParameter(params.valueType);
		if (!Strings.isNullOrEmpty(params.getterDescription)) descriptionBuilder.overrideDescription(params.getterDescription);
		final IMethodDescription description = descriptionBuilder.buildGetter();
		final IPropertyExecutor caller = new GetterExecutor(field, fieldManipulator);
		return new PropertyExecutor(description, caller);
	}

	private IMethodExecutor createSinglePropertySetter(SingleParameters params, final IFieldManipulator fieldManipulator) {
		final PropertyDescriptionBuilder descriptionBuilder = new PropertyDescriptionBuilder(params.name, source);
		descriptionBuilder.addSingleParameter(params.valueType);
		if (!Strings.isNullOrEmpty(params.setterDescription)) descriptionBuilder.overrideDescription(params.setterDescription);
		final IMethodDescription description = descriptionBuilder.buildSetter();
		final IPropertyExecutor caller = new SetterExecutor(field, fieldManipulator);
		return new PropertyExecutor(description, caller);
	}

	private IMethodExecutor createIndexedPropertyGetter(IndexedParameters params, final IIndexedFieldManipulator fieldManipulator) {
		final PropertyDescriptionBuilder descriptionBuilder = new PropertyDescriptionBuilder(params.name, source);
		descriptionBuilder.addIndexParameter(params.docKeyType, params.docValueType);

		if (!Strings.isNullOrEmpty(params.getterDescription)) descriptionBuilder.overrideDescription(params.getterDescription);

		final IMethodDescription description = descriptionBuilder.buildGetter();
		final IPropertyExecutor caller = new IndexedGetterExecutor(field, fieldManipulator, params.keyType);
		return new PropertyExecutor(description, caller);
	}

	private IMethodExecutor createIndexedPropertySetter(IndexedParameters params, final IIndexedFieldManipulator fieldManipulator) {
		final PropertyDescriptionBuilder descriptionBuilder = new PropertyDescriptionBuilder(params.name, source);
		descriptionBuilder.addIndexParameter(params.docKeyType, params.docValueType);

		if (!Strings.isNullOrEmpty(params.setterDescription)) descriptionBuilder.overrideDescription(params.setterDescription);

		final IMethodDescription description = descriptionBuilder.buildSetter();
		final IPropertyExecutor caller = new IndexedSetterExecutor(field, fieldManipulator, params.keyType, params.valueTypeProvider);
		return new PropertyExecutor(description, caller);
	}

	private IMethodExecutor createMergedPropertyGetter(SingleParameters singleParameters, IFieldManipulator singleFieldManipulator, IndexedParameters indexedParameters, IIndexedFieldManipulator indexedFieldManipulator) {
		final PropertyDescriptionBuilder descriptionBuilder = new PropertyDescriptionBuilder(singleParameters.name, source);
		descriptionBuilder.addSingleParameter(singleParameters.valueType);
		descriptionBuilder.addIndexParameter(indexedParameters.docKeyType, indexedParameters.docValueType);

		if (!Strings.isNullOrEmpty(singleParameters.getterDescription)) descriptionBuilder.overrideDescription(singleParameters.getterDescription);
		else if (!Strings.isNullOrEmpty(indexedParameters.getterDescription)) descriptionBuilder.overrideDescription(indexedParameters.getterDescription);

		final IMethodDescription description = descriptionBuilder.buildGetter();
		final IPropertyExecutor caller = new MergedGetterExecutor(field, singleFieldManipulator, indexedFieldManipulator, indexedParameters.keyType);
		return new PropertyExecutor(description, caller);
	}

	private IMethodExecutor createMergedPropertySetter(SingleParameters singleParameters, IFieldManipulator singleFieldManipulator, IndexedParameters indexedParameters, IIndexedFieldManipulator indexedFieldManipulator) {
		final PropertyDescriptionBuilder descriptionBuilder = new PropertyDescriptionBuilder(singleParameters.name, source);
		descriptionBuilder.addSingleParameter(singleParameters.valueType);
		descriptionBuilder.addIndexParameter(indexedParameters.docKeyType, indexedParameters.docValueType);
		if (!Strings.isNullOrEmpty(singleParameters.setterDescription)) descriptionBuilder.overrideDescription(singleParameters.setterDescription);
		else if (!Strings.isNullOrEmpty(singleParameters.setterDescription)) descriptionBuilder.overrideDescription(singleParameters.setterDescription);
		final IMethodDescription description = descriptionBuilder.buildSetter();
		final IPropertyExecutor caller = new MergedSetterExecutor(field, singleFieldManipulator, indexedFieldManipulator, indexedParameters.keyType, indexedParameters.valueTypeProvider);
		return new PropertyExecutor(description, caller);
	}

	private void precheckSingleField(SingleParameters params) {
		final int modifiers = field.getModifiers();
		Preconditions.checkArgument(!Modifier.isStatic(modifiers), "Field marked with @Property can't be static");
		Preconditions.checkArgument(params.readOnly || !Modifier.isFinal(modifiers), "Only fields marked with @Property(readOnly = true) can be marked final");
	}

	private void precheckIndexedField(IndexedParameters params) {
		final int modifiers = field.getModifiers();
		Preconditions.checkArgument(!Modifier.isStatic(modifiers), "Field marked with @IndexedProperty can't be static");
		Preconditions.checkArgument(!params.expandable || !params.readOnly, "@IndexedProperty fields can't be both read-only and expandable");
		Preconditions.checkArgument(params.readOnly || !Modifier.isFinal(modifiers), "Only fields marked with @IndexedProperty(readOnly = true) can be marked final");
		Preconditions.checkArgument(!params.expandable || !Modifier.isFinal(modifiers), "Only non-final @IndexedProperty fields can me expandable");
	}

	public static void buildPropertyList(Class<?> targetCls, String source, List<IMethodExecutor> output) {
		for (Field f : targetCls.getDeclaredFields())
			new PropertyListBuilder(f, source).configureFromFieldProperties().addMethods(output);
	}

}

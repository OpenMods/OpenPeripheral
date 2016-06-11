package openperipheral.adapter.property;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import openmods.reflection.TypeUtils;
import openperipheral.adapter.AnnotationMetaExtractor;
import openperipheral.adapter.IMethodDescription;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.types.TypeHelper;
import openperipheral.api.adapter.CallbackProperty;
import openperipheral.api.adapter.IIndexedPropertyCallback;
import openperipheral.api.adapter.IPropertyCallback;
import openperipheral.api.adapter.IndexedCallbackProperty;
import openperipheral.api.adapter.IndexedProperty;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.property.GetTypeFromField;
import openperipheral.api.property.IIndexedPropertyListener;
import openperipheral.api.property.ISinglePropertyListener;

public class PropertyListBuilder {

	private class Parameters {
		public final String name;
		public final String getterDescription;
		public final String setterDescription;
		public final boolean isDelegating;
		public final boolean readOnly;
		public final boolean valueNullable;

		public Parameters(String name, String getterDescription, String setterDescription, boolean isDelegating, boolean readOnly, boolean valueNullable) {
			this.name = Strings.isNullOrEmpty(name)? field.getName() : name;
			this.getterDescription = getterDescription;
			this.setterDescription = setterDescription;
			this.isDelegating = isDelegating;
			this.readOnly = readOnly;
			this.valueNullable = valueNullable;
		}
	}

	private class SingleParameters extends Parameters {
		public final SingleTypeInfo typeInfo;

		public SingleParameters(String name, String getterDescription, String setterDescription, boolean isDelegating, boolean readOnly, boolean valueNullable, Class<?> valueType, ArgType valueDocType) {
			super(name, getterDescription, setterDescription, isDelegating, readOnly, valueNullable);

			final TypeToken<?> fieldType = TypeUtils.resolveFieldType(ownerClass, field);
			SingleTypeInfoBuilder typeInfoBuilder = new SingleTypeInfoBuilder(fieldType.getType());

			if (valueType != GetTypeFromField.class) typeInfoBuilder.overrideValueType(valueType);
			if (valueDocType != ArgType.AUTO) typeInfoBuilder.overrideValueDocType(TypeHelper.single(valueDocType));

			this.typeInfo = typeInfoBuilder.build();
		}
	}

	private class IndexedParameters extends Parameters {
		public final boolean expandable;
		public final IndexedTypeInfo typeInfo;

		public IndexedParameters(String name, String getterDescription, String setterDescription, boolean isDelegating, boolean readOnly, boolean valueNullable, boolean expandable, Class<?> keyType, ArgType keyDocType, Class<?> valueType, ArgType valueDocType) {
			super(name, getterDescription, setterDescription, isDelegating, readOnly, valueNullable);
			this.expandable = expandable;

			final TypeToken<?> fieldType = TypeUtils.resolveFieldType(ownerClass, field);
			final IndexedTypeInfoBuilder typeInfoBuilder = new IndexedTypeInfoBuilder(fieldType.getType());

			if (keyType != GetTypeFromField.class) typeInfoBuilder.overrideKeyType(keyType);
			if (keyDocType != ArgType.AUTO) typeInfoBuilder.overrideKeyDocType(TypeHelper.single(keyDocType));

			if (valueType != GetTypeFromField.class) typeInfoBuilder.overrideValueType(valueType);
			if (valueDocType != ArgType.AUTO) typeInfoBuilder.overrideValueDocType(TypeHelper.single(valueDocType));

			this.typeInfo = typeInfoBuilder.build();
		}
	}

	private final Class<?> ownerClass;
	private final Field field;
	private final String source;
	private SingleParameters singleParameters;
	private IndexedParameters indexedParameters;

	private final ISinglePropertyAccessHandler singleAccessHandler;
	private final IIndexedPropertyAccessHandler indexedAccessHandler;

	private final Set<String> excludedArchitectures = Sets.newHashSet();
	private final Set<String> featureGroups = Sets.newHashSet();

	public PropertyListBuilder(Class<?> ownerClass, Field field, String source) {
		Preconditions.checkArgument(field.getDeclaringClass().isAssignableFrom(ownerClass), "Field %s not usable on %s", field, ownerClass);
		this.ownerClass = ownerClass;
		this.field = field;
		this.source = source;

		this.singleAccessHandler = getSingleAccessHandler(ownerClass);
		this.indexedAccessHandler = getIndexedAccessHandler(ownerClass);
	}

	private static ISinglePropertyAccessHandler getSingleAccessHandler(Class<?> ownerClass) {
		return (ISinglePropertyListener.class.isAssignableFrom(ownerClass))? ISinglePropertyAccessHandler.DELEGATE_TO_OWNER : ISinglePropertyAccessHandler.IGNORE;
	}

	private static IIndexedPropertyAccessHandler getIndexedAccessHandler(Class<?> ownerClass) {
		return (IIndexedPropertyListener.class.isAssignableFrom(ownerClass))? IIndexedPropertyAccessHandler.DELEGATE_TO_OWNER : IIndexedPropertyAccessHandler.IGNORE;
	}

	public void addSingle(String name, String getterDescription, String setterDescription, boolean isDelegating, boolean readOnly, boolean valueNullable, Class<?> valueType, ArgType docType) {
		this.singleParameters = new SingleParameters(name, getterDescription, setterDescription, isDelegating, readOnly, valueNullable, valueType, docType);
	}

	public void addProperty(Property property) {
		addSingle(property.name(), property.getterDesc(), property.setterDesc(), false, property.readOnly(), property.nullable(), GetTypeFromField.class, property.type());
	}

	public void addProperty(CallbackProperty property) {
		addSingle(property.name(), property.getterDesc(), property.setterDesc(), true, property.readOnly(), property.nullable(), property.javaType(), property.type());
	}

	public void addIndexed(String name, String getterDescription, String setterDescription, boolean isDelegating, boolean readOnly, boolean valueNullable, boolean expandable, Class<?> keyType, ArgType keyDocType, Class<?> valueType, ArgType valueDocType) {
		this.indexedParameters = new IndexedParameters(name, getterDescription, setterDescription, isDelegating, readOnly, valueNullable, expandable, keyType, keyDocType, valueType, valueDocType);
	}

	public void addProperty(IndexedProperty property) {
		addIndexed(property.name(), property.getterDesc(), property.setterDesc(), false, property.readOnly(), property.nullable(), property.expandable(), GetTypeFromField.class, property.keyType(), GetTypeFromField.class, ArgType.AUTO);
	}

	public void addProperty(IndexedCallbackProperty property) {
		addIndexed(property.name(), property.getterDesc(), property.setterDesc(), true, property.readOnly(), property.nullable(), false, property.keyType(), property.keyDocType(), property.valueType(), property.valueDocType());
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

	public PropertyListBuilder configureFromFieldMeta(AnnotationMetaExtractor metaInfo) {
		excludedArchitectures.addAll(metaInfo.getExcludedArchitectures(field));
		featureGroups.addAll(metaInfo.getFeatureGroups(field));
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
		final IFieldManipulator fieldManipulator = SingleManipulatorProvider.getProvider(field.getType(), params.isDelegating);
		output.add(createSinglePropertyGetter(params, fieldManipulator));
		if (!params.readOnly) output.add(createSinglePropertySetter(params, fieldManipulator));
	}

	private void addIndexedPropertyMethods(List<IMethodExecutor> output, IndexedParameters params) {
		precheckIndexedField(params);
		final IIndexedFieldManipulator fieldManipulator = IndexedManipulatorProvider.getProvider(field.getType(), params.isDelegating, params.expandable);
		output.add(createIndexedPropertyGetter(params, fieldManipulator));
		if (!params.readOnly) output.add(createIndexedPropertySetter(params, fieldManipulator));
	}

	private void addMergedPropertyMethods(List<IMethodExecutor> output, SingleParameters singleParameters, IndexedParameters indexedParameters) {
		precheckSingleField(singleParameters);
		precheckIndexedField(indexedParameters);

		final Class<?> fieldType = field.getType();
		final IFieldManipulator singleFieldManipulator = SingleManipulatorProvider.getProvider(fieldType, singleParameters.isDelegating);
		final IIndexedFieldManipulator indexedFieldManipulator = IndexedManipulatorProvider.getProvider(fieldType, indexedParameters.isDelegating, indexedParameters.expandable);

		output.add(createMergedPropertyGetter(singleParameters, singleFieldManipulator, indexedParameters, indexedFieldManipulator));

		if (!singleParameters.readOnly && !indexedParameters.readOnly) {
			output.add(createMergedPropertySetter(singleParameters, singleFieldManipulator, indexedParameters, indexedFieldManipulator));
		} else if (!indexedParameters.readOnly) {
			output.add(createIndexedPropertySetter(indexedParameters, indexedFieldManipulator));
		} else if (!singleParameters.readOnly) {
			output.add(createSinglePropertySetter(singleParameters, singleFieldManipulator));
		}
	}

	private PropertyExecutor createPropertyExecutor(IMethodDescription description, IPropertyExecutor caller) {
		return new PropertyExecutor(description, caller, excludedArchitectures, featureGroups);
	}

	private IMethodExecutor createSinglePropertyGetter(SingleParameters params, final IFieldManipulator fieldManipulator) {
		final PropertyDescriptionBuilder descriptionBuilder = new PropertyDescriptionBuilder(params.name, source);
		descriptionBuilder.addSingleParameter(params.typeInfo);
		if (!Strings.isNullOrEmpty(params.getterDescription)) descriptionBuilder.overrideDescription(params.getterDescription);
		final IMethodDescription description = descriptionBuilder.buildGetter();
		final IPropertyExecutor caller = new GetterExecutor(field, fieldManipulator, singleAccessHandler);
		return createPropertyExecutor(description, caller);
	}

	private IMethodExecutor createSinglePropertySetter(SingleParameters params, final IFieldManipulator fieldManipulator) {
		final PropertyDescriptionBuilder descriptionBuilder = new PropertyDescriptionBuilder(params.name, source);
		descriptionBuilder.addSingleParameter(params.typeInfo);
		if (!Strings.isNullOrEmpty(params.setterDescription)) descriptionBuilder.overrideDescription(params.setterDescription);
		final IMethodDescription description = descriptionBuilder.buildSetter();
		final IPropertyExecutor caller = new SetterExecutor(field, fieldManipulator, params.typeInfo, singleAccessHandler, params.valueNullable);
		return createPropertyExecutor(description, caller);
	}

	private IMethodExecutor createIndexedPropertyGetter(IndexedParameters params, final IIndexedFieldManipulator fieldManipulator) {
		final PropertyDescriptionBuilder descriptionBuilder = new PropertyDescriptionBuilder(params.name, source);
		descriptionBuilder.addIndexParameter(params.typeInfo);

		if (!Strings.isNullOrEmpty(params.getterDescription)) descriptionBuilder.overrideDescription(params.getterDescription);

		final IMethodDescription description = descriptionBuilder.buildGetter();
		final IPropertyExecutor caller = new IndexedGetterExecutor(field, fieldManipulator, params.typeInfo, indexedAccessHandler);
		return createPropertyExecutor(description, caller);
	}

	private IMethodExecutor createIndexedPropertySetter(IndexedParameters params, final IIndexedFieldManipulator fieldManipulator) {
		final PropertyDescriptionBuilder descriptionBuilder = new PropertyDescriptionBuilder(params.name, source);
		descriptionBuilder.addIndexParameter(params.typeInfo);

		if (!Strings.isNullOrEmpty(params.setterDescription)) descriptionBuilder.overrideDescription(params.setterDescription);

		final IMethodDescription description = descriptionBuilder.buildSetter();
		final IPropertyExecutor caller = new IndexedSetterExecutor(field, fieldManipulator, params.typeInfo, indexedAccessHandler, params.valueNullable);
		return createPropertyExecutor(description, caller);
	}

	private IMethodExecutor createMergedPropertyGetter(SingleParameters singleParameters, IFieldManipulator singleFieldManipulator, IndexedParameters indexedParameters, IIndexedFieldManipulator indexedFieldManipulator) {
		final PropertyDescriptionBuilder descriptionBuilder = new PropertyDescriptionBuilder(singleParameters.name, source);
		descriptionBuilder.addSingleParameter(singleParameters.typeInfo);
		descriptionBuilder.addIndexParameter(indexedParameters.typeInfo);

		if (!Strings.isNullOrEmpty(singleParameters.getterDescription)) descriptionBuilder.overrideDescription(singleParameters.getterDescription);
		else if (!Strings.isNullOrEmpty(indexedParameters.getterDescription)) descriptionBuilder.overrideDescription(indexedParameters.getterDescription);

		final IMethodDescription description = descriptionBuilder.buildGetter();
		final IPropertyExecutor caller = new MergedGetterExecutor(field, singleFieldManipulator, singleAccessHandler, indexedFieldManipulator, indexedParameters.typeInfo, indexedAccessHandler);
		return createPropertyExecutor(description, caller);
	}

	private IMethodExecutor createMergedPropertySetter(SingleParameters singleParameters, IFieldManipulator singleFieldManipulator, IndexedParameters indexedParameters, IIndexedFieldManipulator indexedFieldManipulator) {
		final PropertyDescriptionBuilder descriptionBuilder = new PropertyDescriptionBuilder(singleParameters.name, source);
		descriptionBuilder.addSingleParameter(singleParameters.typeInfo);
		descriptionBuilder.addIndexParameter(indexedParameters.typeInfo);
		if (!Strings.isNullOrEmpty(singleParameters.setterDescription)) descriptionBuilder.overrideDescription(singleParameters.setterDescription);
		else if (!Strings.isNullOrEmpty(singleParameters.setterDescription)) descriptionBuilder.overrideDescription(singleParameters.setterDescription);
		final IMethodDescription description = descriptionBuilder.buildSetter();
		final IPropertyExecutor caller = new MergedSetterExecutor(field, singleParameters.valueNullable, singleFieldManipulator, singleParameters.typeInfo, singleAccessHandler, indexedParameters.valueNullable, indexedFieldManipulator, indexedParameters.typeInfo, indexedAccessHandler);
		return createPropertyExecutor(description, caller);
	}

	private void precheckSingleField(SingleParameters params) {
		final int modifiers = field.getModifiers();
		final boolean isFinal = Modifier.isFinal(modifiers);
		final Class<?> fieldType = field.getType();

		Preconditions.checkArgument(!Modifier.isStatic(modifiers), "Field marked with @Property can't be static");
		Preconditions.checkArgument(params.readOnly || !isFinal, "Only fields marked with @Property(readOnly = true) can be marked final");
		Preconditions.checkArgument(!(params.valueNullable && fieldType.isPrimitive()), "Fields with primitive types can't be nullable");
		Preconditions.checkArgument(!params.isDelegating || IPropertyCallback.class.isAssignableFrom(ownerClass), "Only classes implementing IPropertyCallback can use @CallbackProperty");
	}

	private void precheckIndexedField(IndexedParameters params) {
		final int modifiers = field.getModifiers();
		final boolean isFinal = Modifier.isFinal(modifiers);

		Preconditions.checkArgument(!Modifier.isStatic(modifiers), "Field marked with @IndexedProperty can't be static");
		Preconditions.checkArgument(!params.expandable || !params.readOnly, "@IndexedProperty fields can't be both read-only and expandable");
		Preconditions.checkArgument(!params.expandable || !isFinal, "Only non-final @IndexedProperty fields can me expandable");
		Preconditions.checkArgument(!params.isDelegating || IIndexedPropertyCallback.class.isAssignableFrom(ownerClass), "Only classes implementing IIndexedPropertyCallback can use @CallbackIndexedProperty");
	}

	public static void buildPropertyList(Class<?> rootClass, Class<?> targetCls, String source, AnnotationMetaExtractor metaInfo, List<IMethodExecutor> output) {
		for (Field f : targetCls.getDeclaredFields())
			new PropertyListBuilder(rootClass, f, source)
					.configureFromFieldMeta(metaInfo)
					.configureFromFieldProperties()
					.addMethods(output);
	}

}

package openperipheral.adapter.property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import openperipheral.adapter.IMethodDescription;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.method.LuaTypeQualifier;
import openperipheral.api.adapter.CallbackProperty;
import openperipheral.api.adapter.IPropertyCallback;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.ArgType;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class PropertyListBuilder {

	private static void addMethods(List<IMethodExecutor> output, Field field, String source, String name, ArgType type, String getterDescription, String setterDescription, boolean isDelegating, boolean readOnly) {
		int modifiers = field.getModifiers();
		Preconditions.checkArgument((readOnly || !Modifier.isFinal(modifiers)) && !Modifier.isStatic(modifiers), "Field marked with @Property can't be either final or static");
		field.setAccessible(true);

		if (type == ArgType.AUTO) {
			Class<?> fieldType = field.getType();
			type = LuaTypeQualifier.qualifyArgType(fieldType);
		}

		if (Strings.isNullOrEmpty(name)) name = field.getName();

		final IFieldManipulator fieldManipulator = FieldManipulatorProviders.getProvider(isDelegating);

		final PropertyDescriptionBuilder descriptionBuilder = new PropertyDescriptionBuilder(name, source, type);
		if (!Strings.isNullOrEmpty(getterDescription)) descriptionBuilder.setGetterDescription(getterDescription);
		if (!Strings.isNullOrEmpty(setterDescription)) descriptionBuilder.setSetterDescription(setterDescription);
		descriptionBuilder.allowValueOnly();

		{
			final IMethodDescription description = descriptionBuilder.buildGetter();
			final IPropertyExecutor caller = new GetterExecutor(field, fieldManipulator);
			final PropertyExecutor executor = new PropertyExecutor(description, caller);
			output.add(executor);
		}

		if (!readOnly) {
			final IMethodDescription description = descriptionBuilder.buildSetter();
			final IPropertyExecutor caller = new SetterExecutor(field, fieldManipulator);
			final PropertyExecutor executor = new PropertyExecutor(description, caller);
			output.add(executor);
		}
	}

	public static void buildPropertyList(Class<?> targetCls, String source, List<IMethodExecutor> output) {
		for (Field f : targetCls.getDeclaredFields()) {
			{
				Property p = f.getAnnotation(Property.class);
				if (p != null) {
					addMethods(output, f, source, p.name(), p.type(), p.getterDesc(), p.setterDesc(), false, p.readOnly());
					continue;
				}
			}

			{
				CallbackProperty p = f.getAnnotation(CallbackProperty.class);
				if (p != null) {
					Preconditions.checkArgument(IPropertyCallback.class.isAssignableFrom(targetCls));
					addMethods(output, f, source, p.name(), p.type(), p.getterDesc(), p.setterDesc(), true, p.readOnly());
				}
			}
		}
	}
}

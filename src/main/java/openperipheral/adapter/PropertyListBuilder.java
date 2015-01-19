package openperipheral.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import openperipheral.TypeConversionRegistry;
import openperipheral.adapter.method.LuaTypeQualifier;
import openperipheral.api.*;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class PropertyListBuilder {

	private static IPropertyCallback createDefaultCallback(final Object owner) {
		return new IPropertyCallback() {

			@Override
			public void setField(Field field, Object value) {
				try {
					field.set(owner, value);
				} catch (Throwable t) {
					throw Throwables.propagate(t);
				}
			}

			@Override
			public Object getField(Field field) {
				try {
					return field.get(owner);
				} catch (Throwable t) {
					throw Throwables.propagate(t);
				}
			}
		};
	}

	public static class PropertyExecutor implements IMethodExecutor {
		private final FieldContext context;

		public PropertyExecutor(FieldContext context) {
			this.context = context;
		}

		@Override
		public IDescriptable description() {
			return context;
		}

		@Override
		public IMethodCall startCall(final Object target) {
			return new IMethodCall() {
				@Override
				public IMethodCall setPositionalArg(int index, Object value) {
					return this; // NO-OP
				}

				@Override
				public IMethodCall setOptionalArg(String name, Object value) {
					return this; // NO-OP
				}

				@Override
				public Object[] call(Object[] args) {
					return context.call(target, args);
				}
			};
		}

		@Override
		public void validateArgs(Map<String, Class<?>> args) {}

		@Override
		public boolean isAsynchronous() {
			return true;
		}
	}

	public abstract static class FieldContext implements IDescriptable {
		private final String name;
		protected final String description;
		protected LuaArgType type;
		protected final Field field;
		private final String source;

		protected FieldContext(String name, String description, LuaArgType type, Field field, String source) {
			this.name = name;
			this.description = description;
			this.type = type;
			this.field = field;
			this.source = source;
		}

		public abstract Object[] call(Object target, Object... args);

		protected abstract IPropertyCallback getCallback(Object target);

		@Override
		public List<String> getNames() {
			return ImmutableList.of(name);
		}

		@Override
		public String source() {
			return source;
		}

		@Override
		public Map<String, Object> describe() {
			Map<String, Object> result = Maps.newHashMap();
			result.put(IDescriptable.DESCRIPTION, description);
			result.put(IDescriptable.SOURCE, source);
			return result;
		}
	}

	private static final List<Object> EMPTY_ARGS = ImmutableList.of();
	private static final List<Object> NO_RETURNS = ImmutableList.of();

	private abstract static class GetterContext extends FieldContext {

		protected GetterContext(String capitalizedName, String description, LuaArgType type, Field field, String source) {
			super("get" + capitalizedName, description, type, field, source);
		}

		@Override
		public Object[] call(Object target, Object... args) {
			Preconditions.checkArgument(args.length == 0, "Getter has no arguments");
			Object result = getCallback(target).getField(field);
			return ArrayUtils.toArray(TypeConversionRegistry.INSTANCE.toLua(result));
		}

		@Override
		public String signature() {
			return "()";
		}

		@Override
		public Map<String, Object> describe() {
			Map<String, Object> result = super.describe();
			result.put(IDescriptable.ARGS, EMPTY_ARGS);
			result.put(IDescriptable.RETURN_TYPES, ImmutableList.of(type.toString()));
			return result;
		}
	}

	private abstract static class SetterContext extends FieldContext {

		protected SetterContext(String capitalizedName, String description, LuaArgType type, Field field, String source) {
			super("set" + capitalizedName, description, type, field, source);
		}

		@Override
		public Object[] call(Object target, Object... args) {
			Preconditions.checkArgument(args.length == 1, "Setter must have exactly one argument");
			Object arg = args[0];
			Object converted = TypeConversionRegistry.INSTANCE.fromLua(arg, field.getType());
			Preconditions.checkNotNull(converted, "Invalid value type");
			getCallback(target).setField(field, converted);

			return ArrayUtils.EMPTY_OBJECT_ARRAY;
		}

		@Override
		public String signature() {
			return "(value)";
		}

		@Override
		public Map<String, Object> describe() {
			Map<String, Object> args = Maps.newHashMap();
			args.put(IDescriptable.NAME, "value");
			args.put(IDescriptable.TYPE, type.toString());
			args.put(IDescriptable.DESCRIPTION, "");

			Map<String, Object> result = super.describe();
			result.put(IDescriptable.ARGS, args);
			result.put(IDescriptable.RETURN_TYPES, NO_RETURNS);
			return result;
		}
	}

	private static class DefaultGetterContext extends GetterContext {
		protected DefaultGetterContext(String capitalizedName, String description, LuaArgType type, Field field, String source) {
			super(capitalizedName, description, type, field, source);
		}

		@Override
		protected IPropertyCallback getCallback(Object target) {
			return createDefaultCallback(target);
		}
	}

	private static class DefaultSetterContext extends SetterContext {
		protected DefaultSetterContext(String capitalizedName, String description, LuaArgType type, Field field, String source) {
			super(capitalizedName, description, type, field, source);
		}

		@Override
		protected IPropertyCallback getCallback(Object target) {
			return createDefaultCallback(target);
		}
	}

	private static class DelegatingGetterContext extends GetterContext {

		protected DelegatingGetterContext(String capitalizedName, String description, LuaArgType type, Field field, String source) {
			super(capitalizedName, description, type, field, source);
		}

		@Override
		protected IPropertyCallback getCallback(Object target) {
			Preconditions.checkArgument(target instanceof IPropertyCallback, "Invalid target. Probably not your fault");
			return (IPropertyCallback)target;
		}
	}

	private static class DelegatingSetterContext extends SetterContext {

		protected DelegatingSetterContext(String capitalizedName, String description, LuaArgType type, Field field, String source) {
			super(capitalizedName, description, type, field, source);
		}

		@Override
		protected IPropertyCallback getCallback(Object target) {
			Preconditions.checkArgument(target instanceof IPropertyCallback, "Invalid target. Probably not your fault");
			return (IPropertyCallback)target;
		}
	}

	public interface IPropertyExecutorFactory {
		public IMethodExecutor createExecutor(FieldContext context);
	}

	private static ImmutablePair<GetterContext, SetterContext> createContexts(Field field, String source, String name, LuaArgType type, String getterDescription, String setterDescription, boolean isDelegating, boolean readOnly) {
		int modifiers = field.getModifiers();
		Preconditions.checkArgument((readOnly || !Modifier.isFinal(modifiers)) && !Modifier.isStatic(modifiers), "Field marked with @Property can't be either final or static");
		field.setAccessible(true);

		if (type == LuaArgType.AUTO) {
			Class<?> fieldType = field.getType();
			type = LuaTypeQualifier.qualifyArgType(fieldType);
		}

		if (Strings.isNullOrEmpty(name)) name = field.getName();
		String capitalizedName = StringUtils.capitalize(name);

		if (Strings.isNullOrEmpty(getterDescription)) getterDescription = "Get field '" + name + "' value";
		if (Strings.isNullOrEmpty(setterDescription)) setterDescription = "Set field '" + name + "' value";
		GetterContext getter;
		SetterContext setter;
		if (isDelegating) {
			getter = new DelegatingGetterContext(capitalizedName, getterDescription, type, field, source);
			setter = readOnly? null : new DelegatingSetterContext(capitalizedName, setterDescription, type, field, source);
		} else {
			getter = new DefaultGetterContext(capitalizedName, getterDescription, type, field, source);
			setter = readOnly? null : new DefaultSetterContext(capitalizedName, setterDescription, type, field, source);
		}

		return ImmutablePair.of(getter, setter);
	}

	private static void addMethods(Pair<GetterContext, SetterContext> context, List<IMethodExecutor> output) {
		IMethodExecutor getter = new PropertyExecutor(context.getLeft());
		output.add(getter);

		if (context.getRight() != null) {
			IMethodExecutor setter = new PropertyExecutor(context.getRight());
			output.add(setter);
		}
	}

	public static void buildPropertyList(Class<?> targetCls, String source, List<IMethodExecutor> output) {
		for (Field f : targetCls.getDeclaredFields()) {
			{
				Property p = f.getAnnotation(Property.class);
				if (p != null) {
					addMethods(createContexts(f, source, p.name(), p.type(), p.getterDesc(), p.setterDesc(), false, p.readOnly()), output);
					continue;
				}
			}

			{
				CallbackProperty p = f.getAnnotation(CallbackProperty.class);
				if (p != null) {
					Preconditions.checkArgument(IPropertyCallback.class.isAssignableFrom(targetCls));
					addMethods(createContexts(f, source, p.name(), p.type(), p.getterDesc(), p.setterDesc(), true, p.readOnly()), output);
				}
			}
		}
	}
}

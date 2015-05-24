package openperipheral.adapter.property;

import java.util.List;

import openperipheral.adapter.ArgumentDescriptionBase;
import openperipheral.adapter.IMethodDescription;
import openperipheral.adapter.IMethodDescription.IArgumentDescription;
import openperipheral.adapter.types.*;
import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.adapter.method.ReturnType;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class PropertyDescriptionBuilder {
	private static class IndexArgumentDescription extends ArgumentDescriptionBase {
		private final boolean isOptional;

		private IndexArgumentDescription(String name, ArgType type, String description, boolean isOptional) {
			super(name, type, description);
			this.isOptional = isOptional;
		}

		@Override
		public boolean optional() {
			return isOptional;
		}
	}

	private final String name;
	private final String capitalizedName;
	private final String source;

	private final ArgType valueArgumentType;
	private ArgType indexArgumentType;
	private boolean valueOnlyAllowed;

	private String getterDescription;
	private String setterDescription;

	public PropertyDescriptionBuilder(String name, String source, ArgType type) {
		this.name = name;
		this.capitalizedName = StringUtils.capitalize(name);
		this.source = source;
		this.valueArgumentType = type;
	}

	public void allowValueOnly() {
		this.valueOnlyAllowed = true;
	}

	public void addIndexParameter(ArgType type) {
		this.indexArgumentType = type;
	}

	public void setGetterDescription(String getterDescription) {
		this.getterDescription = getterDescription;
	}

	public void setSetterDescription(String setterDescription) {
		this.setterDescription = setterDescription;
	}

	public IMethodDescription buildSetter() {
		Preconditions.checkState(valueOnlyAllowed || indexArgumentType != null, "Invalid combination of options: no index argument, but value-only is disabled");
		String description = Strings.isNullOrEmpty(this.setterDescription)? "Set field '" + name + "' value" : this.setterDescription;
		final String methodName = "set" + capitalizedName;
		final List<IArgumentDescription> arguments = Lists.newArrayList();

		arguments.add(createValueArgument());
		if (indexArgumentType != null) arguments.add(createIndexArgument());

		return new SimpleMethodDescription(methodName, description, source, arguments, IReturnType.VOID);
	}

	public IMethodDescription buildGetter() {
		Preconditions.checkState(valueOnlyAllowed || indexArgumentType != null, "Invalid combination of options: no index argument, but value-only is disabled");
		String description = Strings.isNullOrEmpty(this.getterDescription)? "Get field '" + name + "' value" : this.getterDescription;
		final String methodName = "get" + capitalizedName;

		final List<IArgumentDescription> arguments = Lists.newArrayList();
		if (indexArgumentType != null) arguments.add(createIndexArgument());

		final IReturnType returnType = calculateReturnType(valueArgumentType, indexArgumentType);

		return new SimpleMethodDescription(methodName, description, source, arguments, returnType);
	}

	private ArgumentDescriptionBase createValueArgument() {
		return new ArgumentDescriptionBase("value", valueArgumentType, "");
	}

	private IndexArgumentDescription createIndexArgument() {
		return new IndexArgumentDescription("index", indexArgumentType, "", valueOnlyAllowed);
	}

	private IReturnType calculateReturnType(ArgType valueType, ArgType indexType) {
		if (!valueOnlyAllowed) {
			Preconditions.checkNotNull(indexType);
			ReturnType indexReturnType = TypeHelper.convert(indexArgumentType);
			return new SingleReturnType(indexReturnType);
		}

		final ReturnType valueReturnType = TypeHelper.convert(valueArgumentType);
		final SingleReturnType wrappedValueType = new SingleReturnType(valueReturnType);

		if (indexType == null) return wrappedValueType;

		final ReturnType indexReturnType = TypeHelper.convert(indexArgumentType);

		if (indexReturnType == valueReturnType) return wrappedValueType;

		final SingleReturnType wrappedReturnType = new SingleReturnType(indexReturnType);

		return new AlternativeReturnType(wrappedValueType, wrappedReturnType);
	}

}

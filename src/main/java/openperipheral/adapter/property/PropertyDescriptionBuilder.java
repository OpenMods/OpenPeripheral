package openperipheral.adapter.property;

import java.util.List;

import openperipheral.adapter.ArgumentDescriptionBase;
import openperipheral.adapter.IMethodDescription;
import openperipheral.adapter.IMethodDescription.IArgumentDescription;
import openperipheral.adapter.types.AlternativeType;
import openperipheral.adapter.types.SingleType;
import openperipheral.adapter.types.TypeHelper;
import openperipheral.api.adapter.IScriptType;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class PropertyDescriptionBuilder {
	private static final String ARG_VALUE = "value";
	private static final String ARG_INDEX = "index";

	private static class IndexArgumentDescription extends ArgumentDescriptionBase {
		private final boolean isOptional;

		private IndexArgumentDescription(String name, IScriptType type, String description, boolean isOptional) {
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

	private IScriptType singleValueType;
	private IScriptType indexKeyType;
	private IScriptType indexValueType;

	private boolean buildIndexedProperty;
	private boolean buildSingleProperty;

	private String description;

	public PropertyDescriptionBuilder(String name, String source) {
		this.name = name;
		this.capitalizedName = StringUtils.capitalize(name);
		this.source = source;
	}

	public void addSingleParameter(IScriptType singleType) {
		this.singleValueType = singleType;
		this.buildSingleProperty = true;
	}

	public void addIndexParameter(IScriptType keyType, IScriptType valueType) {
		this.indexKeyType = keyType;
		this.indexValueType = valueType;
		this.buildIndexedProperty = true;
	}

	public void overrideDescription(String description) {
		this.description = description;
	}

	public IMethodDescription buildSetter() {
		String description = Strings.isNullOrEmpty(this.description)? "Set field '" + name + "' value" : this.description;
		final String methodName = "set" + capitalizedName;
		final List<IArgumentDescription> arguments = Lists.newArrayList();

		final IScriptType valueType = calculateValueType();

		arguments.add(new ArgumentDescriptionBase(ARG_VALUE, valueType, ""));
		if (buildIndexedProperty) arguments.add(createIndexArgument());

		return new SimpleMethodDescription(methodName, description, source, arguments, SingleType.VOID);
	}

	public IMethodDescription buildGetter() {
		String description = Strings.isNullOrEmpty(this.description)? "Get field '" + name + "' value" : this.description;
		final String methodName = "get" + capitalizedName;

		final List<IArgumentDescription> arguments = Lists.newArrayList();
		if (buildIndexedProperty) arguments.add(createIndexArgument());

		final IScriptType returnType = calculateValueType();

		return new SimpleMethodDescription(methodName, description, source, arguments, returnType);
	}

	private IScriptType calculateValueType() {
		if (buildIndexedProperty && buildSingleProperty) {
			if (TypeHelper.compareTypes(singleValueType, indexValueType)) {
				return singleValueType;
			} else {
				return new AlternativeType(singleValueType, indexValueType);
			}
		}

		if (buildSingleProperty) return singleValueType;
		if (buildIndexedProperty) return indexValueType;
		throw new IllegalStateException("DERP?");
	}

	private IndexArgumentDescription createIndexArgument() {
		return new IndexArgumentDescription(ARG_INDEX, indexKeyType, "", buildSingleProperty);
	}

}

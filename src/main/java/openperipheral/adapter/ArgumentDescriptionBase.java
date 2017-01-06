package openperipheral.adapter;

import openperipheral.adapter.IMethodDescription.IArgumentDescription;
import openperipheral.api.adapter.IScriptType;

public abstract class ArgumentDescriptionBase implements IArgumentDescription {

	public static class Simple extends ArgumentDescriptionBase {
		private IScriptType type;

		public Simple(String name, String description, IScriptType type) {
			super(name, description);
			this.type = type;
		}

		@Override
		public IScriptType type() {
			return type;
		}

	}

	protected final String name;

	protected String description;

	public ArgumentDescriptionBase(String name, String description) {
		this.name = name;
		this.description = description;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String description() {
		return description;
	}

	@Override
	public boolean is(IAttributeProperty property) {
		return false;
	}

}

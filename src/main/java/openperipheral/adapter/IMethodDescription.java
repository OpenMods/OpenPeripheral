package openperipheral.adapter;

import java.util.List;
import openperipheral.api.adapter.IScriptType;

public interface IMethodDescription {

	public interface IArgumentDescription {
		public String name();

		public IScriptType type();

		public String description();

		public boolean is(IAttributeProperty property);
	}

	public String source();

	public List<String> getNames();

	public String description();

	public List<IArgumentDescription> arguments();

	public IScriptType returnTypes();
}

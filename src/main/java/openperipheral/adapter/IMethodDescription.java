package openperipheral.adapter;

import java.util.List;
import java.util.Set;

import openperipheral.api.adapter.IScriptType;

public interface IMethodDescription {
	public interface IArgumentDescription {
		public String name();

		public IScriptType type();

		public String description();

		public boolean nullable();

		public boolean optional();

		public boolean variadic();

		public Set<String> attributes();
	}

	public String source();

	public List<String> getNames();

	public String description();

	public List<IArgumentDescription> arguments();

	public IScriptType returnTypes();

	public Set<String> attributes();
}

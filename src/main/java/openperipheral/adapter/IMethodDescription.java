package openperipheral.adapter;

import java.util.List;
import java.util.Set;

import openperipheral.adapter.types.IReturnType;
import openperipheral.api.adapter.method.ArgType;

public interface IMethodDescription {
	public interface IArgumentDescription {
		public String name();

		public ArgType type();

		public String range();

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

	public IReturnType returnTypes();

	public Set<String> attributes();
}

package openperipheral.adapter;

import java.util.List;
import java.util.Set;

import openperipheral.adapter.types.IType;

public interface IMethodDescription {
	public interface IArgumentDescription {
		public String name();

		public IType type();

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

	public IType returnTypes();

	public Set<String> attributes();
}

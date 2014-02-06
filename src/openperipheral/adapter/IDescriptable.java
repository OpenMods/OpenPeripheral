package openperipheral.adapter;

import java.util.List;
import java.util.Map;

public interface IDescriptable {
	// Map can actually contain anything, but these names are used in 'docs'
	// program
	static final String ARGS = "args";
	static final String RETURN_TYPES = "returnTypes";
	static final String DESCRIPTION = "description";

	// Additional fields names for args
	static final String NAME = "name";
	static final String TYPE = "type";

	public List<String> getNames();

	public String signature();

	public Map<String, Object> describe();
}

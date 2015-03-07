package openperipheral.api.meta;

import java.util.Map;
import java.util.Set;

import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.method.Arg;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;

@AdapterSourceName("properties")
public interface IMetaProviderProxy {
	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Returns all properties as table")
	public Map<String, Object> all();

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Returns basic properties as table")
	public Map<String, Object> basic();

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Returns value of selected property")
	public Object single(@Arg(name = "key", description = "Id of property. Must be one from returned by keys()") String key);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Returns value of selected properties")
	public Map<String, Object> select(@Arg(name = "keys", description = "Id of property. Must be subset of ones returned from keys()") String... keys);

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Returns all available property keys")
	public Set<String> keys();
}

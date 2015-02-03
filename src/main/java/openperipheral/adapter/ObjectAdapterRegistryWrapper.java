package openperipheral.adapter;

import openperipheral.ApiImplementation;
import openperipheral.api.adapter.IObjectAdapter;
import openperipheral.api.adapter.IObjectAdapterRegistry;

@ApiImplementation
public class ObjectAdapterRegistryWrapper implements IObjectAdapterRegistry {
	@Override
	public boolean register(IObjectAdapter adapter) {
		return AdapterRegistry.OBJECT_ADAPTERS.addAdapter(adapter);
	}

	@Override
	public void registerInline(Class<?> cls) {
		AdapterRegistry.OBJECT_ADAPTERS.addInlineAdapter(cls);
	}

}

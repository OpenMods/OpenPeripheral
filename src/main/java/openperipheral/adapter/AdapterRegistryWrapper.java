package openperipheral.adapter;

import openperipheral.ApiImplementation;
import openperipheral.api.IAdapterRegistry;
import openperipheral.api.IObjectAdapter;
import openperipheral.api.IPeripheralAdapter;

@ApiImplementation
public class AdapterRegistryWrapper implements IAdapterRegistry {

	@Override
	public boolean register(IPeripheralAdapter adapter) {
		return AdapterRegistry.PERIPHERAL_ADAPTERS.addAdapter(adapter);
	}

	@Override
	public boolean register(IObjectAdapter adapter) {
		return AdapterRegistry.OBJECT_ADAPTERS.addAdapter(adapter);
	}

	@Override
	public void registerInline(Class<?> cls) {
		AdapterRegistry.PERIPHERAL_ADAPTERS.addInlineAdapter(cls);
	}

}

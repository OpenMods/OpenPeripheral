package openperipheral.adapter;

import openperipheral.ApiImplementation;
import openperipheral.api.IAdapterRegistry;
import openperipheral.api.IObjectAdapter;
import openperipheral.api.IPeripheralAdapter;

@ApiImplementation
public class AdapterRegistryWrapper implements IAdapterRegistry {

	@Override
	public void register(IPeripheralAdapter adapter) {
		AdapterManager.addPeripheralAdapter(adapter);
	}

	@Override
	public void register(IObjectAdapter adapter) {
		AdapterManager.addObjectAdapter(adapter);
	}

	@Override
	public void registerInline(Class<?> cls) {
		AdapterManager.addInlinePeripheralAdapter(cls);
	}

}

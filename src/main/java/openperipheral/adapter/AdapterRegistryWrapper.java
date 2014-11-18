package openperipheral.adapter;

import openperipheral.ApiImplementation;
import openperipheral.api.IAdapterRegistry;
import openperipheral.api.IObjectAdapter;
import openperipheral.api.IPeripheralAdapter;

@ApiImplementation
public class AdapterRegistryWrapper implements IAdapterRegistry {

	@Override
	public boolean register(IPeripheralAdapter adapter) {
		return AdapterManager.addPeripheralAdapter(adapter);
	}

	@Override
	public boolean register(IObjectAdapter adapter) {
		return AdapterManager.addObjectAdapter(adapter);
	}

	@Override
	public void registerInline(Class<?> cls) {
		AdapterManager.addInlinePeripheralAdapter(cls);
	}

}

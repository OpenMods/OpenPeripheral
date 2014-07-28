package openperipheral.implementations;

import openperipheral.adapter.AdapterManager;
import openperipheral.api.IAdapterRegistry;
import openperipheral.api.IObjectAdapter;
import openperipheral.api.IPeripheralAdapter;

@ApiImplementation
public class AdapterRegistryImpl implements IAdapterRegistry {

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

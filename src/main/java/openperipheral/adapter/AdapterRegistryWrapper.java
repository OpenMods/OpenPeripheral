package openperipheral.adapter;

import openperipheral.api.adapter.*;

public class AdapterRegistryWrapper<T extends IAdapter> implements IAdapterRegistry<T> {

	public static class Objects extends AdapterRegistryWrapper<IObjectAdapter> implements IObjectAdapterRegistry {
		public Objects() {
			super(AdapterRegistry.OBJECT_ADAPTERS);
		}
	}

	public static class Peripherals extends AdapterRegistryWrapper<IPeripheralAdapter> implements IPeripheralAdapterRegistry {
		public Peripherals() {
			super(AdapterRegistry.PERIPHERAL_ADAPTERS);
		}
	}

	private final AdapterRegistry registry;

	public AdapterRegistryWrapper(AdapterRegistry registry) {
		this.registry = registry;
	}

	@Override
	public boolean register(T adapter) {
		return registry.addAdapter(adapter);
	}

	@Override
	@Deprecated
	public void registerInline(Class<?> cls) {
		// No longer in use!
	}

}

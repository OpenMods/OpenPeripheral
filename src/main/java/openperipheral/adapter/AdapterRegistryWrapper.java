package openperipheral.adapter;

import openmods.access.ApiImplementation;
import openperipheral.api.adapter.*;

public class AdapterRegistryWrapper<T extends IAdapter> implements IAdapterRegistry<T> {

	@ApiImplementation
	public static class Objects extends AdapterRegistryWrapper<IObjectAdapter> implements IObjectAdapterRegistry {
		public Objects() {
			super(AdapterRegistry.OBJECT_ADAPTERS);
		}
	}

	@ApiImplementation
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
	public void registerInline(Class<?> cls) {
		registry.addInlineAdapter(cls);
	}

}

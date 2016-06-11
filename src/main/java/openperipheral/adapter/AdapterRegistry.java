package openperipheral.adapter;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import openmods.Log;
import openperipheral.adapter.wrappers.AdapterWrapper;
import openperipheral.adapter.wrappers.ExternalAdapterWrapper;
import openperipheral.api.adapter.IAdapter;
import openperipheral.api.adapter.IAdapterWithConstraints;

public class AdapterRegistry {

	public static final AdapterRegistry PERIPHERAL_ADAPTERS = new AdapterRegistry();

	public static final AdapterRegistry OBJECT_ADAPTERS = new AdapterRegistry();

	private final Multimap<Class<?>, AdapterWrapper> externalAdapters = HashMultimap.create();

	public Map<Class<?>, Collection<AdapterWrapper>> listExternalAdapters() {
		return Collections.unmodifiableMap(externalAdapters.asMap());
	}

	public boolean addAdapter(IAdapter adapter) {
		final AdapterWrapper wrapper;
		try {
			wrapper = wrapExternalAdapter(adapter);
		} catch (Throwable e) {
			Log.warn(e, "Something went terribly wrong while adding internal adapter '%s'. It will be disabled", adapter.getClass());
			return false;
		}
		final Class<?> targetCls = adapter.getTargetClass();
		Preconditions.checkArgument(!Object.class.equals(targetCls), "Can't add adapter for Object class");

		Log.trace("Registering %s adapter (source id: %s) for %s", wrapper.describe(), wrapper.source(), targetCls);
		externalAdapters.put(targetCls, wrapper);
		return true;
	}

	public Collection<AdapterWrapper> getExternalAdapters(Class<?> targetCls) {
		return Collections.unmodifiableCollection(externalAdapters.get(targetCls));
	}

	protected AdapterWrapper wrapExternalAdapter(IAdapter adapter) {
		return adapter instanceof IAdapterWithConstraints
				? new ExternalAdapterWrapper.WithConstraints((IAdapterWithConstraints)adapter)
				: new ExternalAdapterWrapper(adapter);
	}
}

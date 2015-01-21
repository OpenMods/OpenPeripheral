package openperipheral.adapter;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import openmods.Log;
import openperipheral.adapter.wrappers.AdapterWrapper;
import openperipheral.adapter.wrappers.ExternalAdapterWrapper;
import openperipheral.adapter.wrappers.InlineAdapterWrapper;
import openperipheral.api.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class AdapterRegistry {

	public static final AdapterRegistry PERIPHERAL_ADAPTERS = new AdapterRegistry();

	public static final AdapterRegistry OBJECT_ADAPTERS = new AdapterRegistry();

	private final Multimap<Class<?>, AdapterWrapper> externalAdapters = HashMultimap.create();

	private final Map<Class<?>, AdapterWrapper> internalAdapters = Maps.newHashMap();

	public Map<Class<?>, Collection<AdapterWrapper>> listExternalAdapters() {
		return Collections.unmodifiableMap(externalAdapters.asMap());
	}

	public Map<Class<?>, AdapterWrapper> listInternalAdapters() {
		return Collections.unmodifiableMap(internalAdapters);
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

	public void addInlineAdapter(Class<?> targetCls) {
		AdapterWrapper wrapper = wrapInlineAdapter(targetCls);
		Log.trace("Registering %s adapter (source id: %s) adapter for %s", wrapper.describe(), wrapper.source(), targetCls);
		internalAdapters.put(targetCls, wrapper);
	}

	public Collection<AdapterWrapper> getExternalAdapters(Class<?> targetCls) {
		return Collections.unmodifiableCollection(externalAdapters.get(targetCls));
	}

	public AdapterWrapper getInlineAdapter(Class<?> targetCls) {
		AdapterWrapper wrapper = internalAdapters.get(targetCls);
		if (wrapper == null) {
			wrapper = wrapInlineAdapter(targetCls);
			internalAdapters.put(targetCls, wrapper);
		}

		return wrapper;
	}

	protected AdapterWrapper wrapExternalAdapter(IAdapter adapter) {
		return adapter instanceof IAdapterWithConstraints
				? new ExternalAdapterWrapper.WithConstraints((IAdapterWithConstraints)adapter)
				: new ExternalAdapterWrapper(adapter);
	}

	protected AdapterWrapper wrapInlineAdapter(Class<?> targetClass) {
		return new InlineAdapterWrapper(targetClass, getSourceId(targetClass));
	}

	private static String getSourceId(Class<?> cls) {
		{
			AdapterSourceName id = cls.getAnnotation(AdapterSourceName.class);
			if (id != null) return id.value();
		}

		if (TileEntity.class.isAssignableFrom(cls)) {
			PeripheralTypeId id = cls.getAnnotation(PeripheralTypeId.class);
			if (id != null) return id.value();
		}

		Log.trace("Inline adapter %s has no @AdapterSourceName annotation", cls);
		return cls.getName().toLowerCase();
	}
}

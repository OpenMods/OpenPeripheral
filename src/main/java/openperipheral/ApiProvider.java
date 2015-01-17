package openperipheral;

import java.lang.reflect.Modifier;
import java.util.*;

import openmods.Log;
import openperipheral.adapter.AdapterRegistryWrapper;
import openperipheral.adapter.TileEntityBlacklist;
import openperipheral.api.ApiAccess;
import openperipheral.api.IApiInterface;
import openperipheral.interfaces.cc.providers.AdapterFactoryWrapper;
import openperipheral.meta.EntityMetadataBuilder;
import openperipheral.meta.ItemStackMetadataBuilder;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ApiProvider implements ApiAccess.ApiProvider {

	private interface IApiInstanceProvider {
		public IApiInterface getInterface();
	}

	private static class SingleInstanceProvider implements IApiInstanceProvider {
		private final IApiInterface instance;

		public SingleInstanceProvider(Class<? extends IApiInterface> cls) {
			try {
				instance = cls.newInstance();
			} catch (Throwable t) {
				throw Throwables.propagate(t);
			}
		}

		@Override
		public IApiInterface getInterface() {
			return instance;
		}
	}

	private static class NewInstanceProvider implements IApiInstanceProvider {
		private final Class<? extends IApiInterface> cls;

		public NewInstanceProvider(Class<? extends IApiInterface> cls) {
			this.cls = cls;
		}

		@Override
		public IApiInterface getInterface() {
			try {
				return cls.newInstance();
			} catch (Throwable t) {
				throw Throwables.propagate(t);
			}
		}
	}

	private static class SingletonProvider implements IApiInstanceProvider {
		private final IApiInterface obj;

		public SingletonProvider(IApiInterface obj) {
			this.obj = obj;
		}

		@Override
		public IApiInterface getInterface() {
			return obj;
		}
	}

	private final Map<Class<? extends IApiInterface>, IApiInstanceProvider> PROVIDERS = Maps.newHashMap();

	@SuppressWarnings("unchecked")
	private static void listAllImplementedApis(Collection<Class<? extends IApiInterface>> output, Class<?>... intfs) {
		for (Class<?> cls : intfs) {
			Preconditions.checkArgument(cls.isInterface());
			if (cls != IApiInterface.class &&
					IApiInterface.class.isAssignableFrom(cls)) output.add((Class<? extends IApiInterface>)cls);
		}
	}

	private static void addAllInterfaces(Set<Class<? extends IApiInterface>> interfaces) {
		Queue<Class<? extends IApiInterface>> queue = Lists.newLinkedList(interfaces);

		Class<? extends IApiInterface> cls;
		while ((cls = queue.poll()) != null) {
			interfaces.add(cls);
			listAllImplementedApis(queue, cls.getInterfaces());
		}
	}

	private void registerInterfaces(Class<? extends IApiInterface> cls, IApiInstanceProvider provider, final boolean includeSuper) {
		Set<Class<? extends IApiInterface>> implemented = Sets.newHashSet();
		listAllImplementedApis(implemented, cls.getInterfaces());
		if (includeSuper) addAllInterfaces(implemented);

		for (Class<? extends IApiInterface> impl : implemented) {
			IApiInstanceProvider prev = PROVIDERS.put(impl, provider);
			Preconditions.checkState(prev == null, "Conflict on interface %s", impl);
		}
	}

	private void registerClass(Class<? extends IApiInterface> cls) {
		Preconditions.checkArgument(!Modifier.isAbstract(cls.getModifiers()));

		ApiImplementation meta = cls.getAnnotation(ApiImplementation.class);
		Preconditions.checkNotNull(meta);

		IApiInstanceProvider provider = meta.cacheable()? new SingleInstanceProvider(cls) : new NewInstanceProvider(cls);
		registerInterfaces(cls, provider, meta.includeSuper());
	}

	private void registerInstance(IApiInterface obj) {
		final Class<? extends IApiInterface> cls = obj.getClass();

		ApiSingleton meta = cls.getAnnotation(ApiSingleton.class);
		Preconditions.checkNotNull(meta);

		IApiInstanceProvider provider = new SingletonProvider(obj);
		registerInterfaces(cls, provider, meta.includeSuper());
	}

	private ApiProvider() {
		registerClass(AdapterFactoryWrapper.class);
		registerClass(AdapterRegistryWrapper.class);
		registerClass(EntityMetadataBuilder.class);
		registerClass(ItemStackMetadataBuilder.class);

		registerInstance(TypeConversionRegistry.INSTANCE);
		registerInstance(TileEntityBlacklist.INSTANCE);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IApiInterface> T getApi(Class<T> cls) {
		IApiInstanceProvider provider = PROVIDERS.get(cls);
		Preconditions.checkNotNull(provider, "Can't get implementation for class %s", cls);
		return (T)provider.getInterface();
	}

	@Override
	public <T extends IApiInterface> boolean isApiPresent(Class<T> cls) {
		return PROVIDERS.containsKey(cls);
	}

	static void installApi() {
		final String presentApiVersion;
		try {
			presentApiVersion = ApiAccess.API_VERSION;
		} catch (Throwable t) {
			throw new IllegalStateException("Failed to get OpenPeripheralCore API version, class missing?", t);
		}

		String apiSource;
		try {
			apiSource = ApiAccess.class.getProtectionDomain().getCodeSource().getLocation().toString();
		} catch (Throwable t) {
			apiSource = "<unknown, see logs>";
			Log.severe(t, "Failed to get OpenPeripheralCore API source");
		}

		Preconditions.checkState(OpenPeripheralCore.PROVIDED_API_VERSION.equals(presentApiVersion),
				"OpenPeripheralCore version mismatch, should be %s, is %s (ApiAccess source: %s)",
				OpenPeripheralCore.PROVIDED_API_VERSION, presentApiVersion, apiSource
				);

		try {
			ApiAccess.init(new ApiProvider());
		} catch (Throwable t) {
			throw new IllegalStateException(String.format("Failed to register OpenPeripheralCore API provider (ApiAccess source: %s)", apiSource), t);
		}

		Log.info("OPC API v. %s provideded by OpenPeripheralCore, (ApiAccess source: %s)", presentApiVersion, apiSource);
	}
}

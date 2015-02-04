package openperipheral;

import java.lang.reflect.Modifier;
import java.util.*;

import openmods.Log;
import openmods.Mods;
import openperipheral.adapter.AdapterRegistryWrapper;
import openperipheral.adapter.TileEntityBlacklist;
import openperipheral.api.ApiAccess;
import openperipheral.api.IApiInterface;
import openperipheral.converter.TypeConvertersProvider;
import openperipheral.interfaces.cc.ModuleComputerCraft;
import openperipheral.interfaces.oc.ModuleOpenComputers;
import openperipheral.meta.EntityMetadataBuilder;
import openperipheral.meta.ItemStackMetadataBuilder;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.Loader;

public class ApiProvider implements ApiAccess.ApiProvider {

	public interface IApiInstanceProvider<T extends IApiInterface> {
		public T getInterface();
	}

	private static class SingleInstanceProvider<T extends IApiInterface> implements IApiInstanceProvider<T> {
		private final T instance;

		public SingleInstanceProvider(Class<? extends T> cls) {
			try {
				instance = cls.newInstance();
			} catch (Throwable t) {
				throw Throwables.propagate(t);
			}
		}

		@Override
		public T getInterface() {
			return instance;
		}
	}

	private static class NewInstanceProvider<T extends IApiInterface> implements IApiInstanceProvider<T> {
		private final Class<? extends T> cls;

		public NewInstanceProvider(Class<? extends T> cls) {
			this.cls = cls;
		}

		@Override
		public T getInterface() {
			try {
				return cls.newInstance();
			} catch (Throwable t) {
				throw Throwables.propagate(t);
			}
		}
	}

	private static class SingletonProvider<T extends IApiInterface> implements IApiInstanceProvider<T> {
		private final T obj;

		public SingletonProvider(T obj) {
			this.obj = obj;
		}

		@Override
		public T getInterface() {
			return obj;
		}
	}

	private final Map<Class<? extends IApiInterface>, IApiInstanceProvider<?>> PROVIDERS = Maps.newHashMap();

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

	private <T extends IApiInterface> void registerInterfaces(Class<? extends T> cls, IApiInstanceProvider<T> provider, boolean includeSuper) {
		Set<Class<? extends IApiInterface>> implemented = Sets.newHashSet();
		listAllImplementedApis(implemented, cls.getInterfaces());
		if (includeSuper) addAllInterfaces(implemented);

		for (Class<? extends IApiInterface> impl : implemented) {
			IApiInstanceProvider<?> prev = PROVIDERS.put(impl, provider);
			Preconditions.checkState(prev == null, "Conflict on interface %s", impl);
		}
	}

	public <T extends IApiInterface> void registerClass(Class<? extends T> cls) {
		Preconditions.checkArgument(!Modifier.isAbstract(cls.getModifiers()));

		ApiImplementation meta = cls.getAnnotation(ApiImplementation.class);
		Preconditions.checkNotNull(meta);

		IApiInstanceProvider<T> provider = meta.cacheable()? new SingleInstanceProvider<T>(cls) : new NewInstanceProvider<T>(cls);
		registerInterfaces(cls, provider, meta.includeSuper());
	}

	private <T extends IApiInterface> void registerInstance(T obj) {
		@SuppressWarnings("unchecked")
		final Class<? extends T> cls = (Class<? extends T>)obj.getClass();

		ApiSingleton meta = cls.getAnnotation(ApiSingleton.class);
		Preconditions.checkNotNull(meta);

		IApiInstanceProvider<T> provider = new SingletonProvider<T>(obj);
		registerInterfaces(cls, provider, meta.includeSuper());
	}

	private ApiProvider() {
		registerClass(AdapterRegistryWrapper.Peripherals.class);
		registerClass(AdapterRegistryWrapper.Objects.class);
		registerClass(EntityMetadataBuilder.class);
		registerClass(ItemStackMetadataBuilder.class);

		registerInstance(TypeConvertersProvider.INSTANCE);
		registerInstance(TileEntityBlacklist.INSTANCE);

		if (Loader.isModLoaded(Mods.COMPUTERCRAFT)) ModuleComputerCraft.installAPI(this);
		if (Loader.isModLoaded(Mods.OPENCOMPUTERS)) ModuleOpenComputers.installAPI(this);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IApiInterface> T getApi(Class<T> cls) {
		IApiInstanceProvider<?> provider = PROVIDERS.get(cls);
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

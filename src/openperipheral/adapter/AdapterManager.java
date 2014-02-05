package openperipheral.adapter;

import java.util.*;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import openmods.Log;
import openperipheral.adapter.object.*;
import openperipheral.adapter.peripheral.*;
import openperipheral.api.*;
import openperipheral.util.PeripheralUtils;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;

import dan200.computer.api.*;

public abstract class AdapterManager<A extends IAdapterBase, E extends IMethodExecutor> {

	private static final Random RANDOM = new Random();

	private static final String[] BOGUS_METODS = new String[] {
			"help",
			"whats_going_on",
			"wtf",
			"lol_nope",
			"derp",
			"guru_meditation",
			"woof",
			"nothing_to_see_here",
			"kernel_panic",
			"hello_segfault",
			"i_see_dead_bytes",
			"xyzzy",
			"abort_retry_fail_continue"
	};

	private static final IHostedPeripheral PLACEHOLDER = new IHostedPeripheral() {

		@Override
		public String getType() {
			return "broken_peripheral";
		}

		@Override
		public String[] getMethodNames() {
			return ArrayUtils.toArray(BOGUS_METODS[RANDOM.nextInt(BOGUS_METODS.length)]);
		}

		@Override
		public void detach(IComputerAccess computer) {}

		@Override
		public boolean canAttachToSide(int side) {
			return true;
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
			return ArrayUtils.toArray("This peripheral is broken. You can show your log in #OpenMods");
		}

		@Override
		public void attach(IComputerAccess computer) {}

		@Override
		public void writeToNBT(NBTTagCompound nbttagcompound) {}

		@Override
		public void update() {}

		@Override
		public void readFromNBT(NBTTagCompound nbttagcompound) {}
	};

	private static final IPeripheralHandler peripheralHandler = new IPeripheralHandler() {

		private Map<TileEntity, IHostedPeripheral> created = new WeakHashMap<TileEntity, IHostedPeripheral>();

		@Override
		public IHostedPeripheral getPeripheral(TileEntity tile) {
			if (tile == null) return null;

			IHostedPeripheral peripheral = created.get(tile);

			if (peripheral == null) {
				try {
					if (tile instanceof IPeripheralProvider) {
						peripheral = ((IPeripheralProvider)tile).providePeripheral(tile.worldObj);
					} else {
						AdaptedClass<IPeripheralMethodExecutor> adapter = peripherals.adaptClass(tile.getClass());
						peripheral = new HostedPeripheral(adapter, tile);
					}
				} catch (Throwable t) {
					Log.severe(t, "Can't create peripheral for TE %s @ (%d,%d,%d) in world %s",
							tile.getClass(), tile.xCoord, tile.yCoord, tile.zCoord, tile.worldObj.provider.dimensionId);
					peripheral = PLACEHOLDER;

				}

				created.put(tile, peripheral);
			}

			return peripheral;
		}
	};

	public static final AdapterManager<IObjectAdapter, IObjectMethodExecutor> objects = new AdapterManager<IObjectAdapter, IObjectMethodExecutor>() {

		@Override
		protected AdaptedClass<IObjectMethodExecutor> adaptClass(Class<?> targetClass) {
			return new ObjectAdaptedClass(this, targetClass);
		}

		@Override
		protected AdapterWrapper<IObjectMethodExecutor> wrapExternalAdapter(IObjectAdapter adapter) {
			return new ObjectAdapterWrapper.External(adapter);
		}

		@Override
		protected AdapterWrapper<IObjectMethodExecutor> wrapInlineAdapter(Class<?> targetClass) {
			return new ObjectAdapterWrapper.Inline(targetClass);
		}
	};

	public static final AdapterManager<IPeripheralAdapter, IPeripheralMethodExecutor> peripherals = new AdapterManager<IPeripheralAdapter, IPeripheralMethodExecutor>() {
		@Override
		protected AdaptedClass<IPeripheralMethodExecutor> adaptClass(Class<?> targetClass) {
			return new PeripheralAdaptedClass(this, targetClass);
		}

		@Override
		protected AdapterWrapper<IPeripheralMethodExecutor> wrapExternalAdapter(IPeripheralAdapter adapter) {
			return new PeripheralExternalAdapterWrapper(adapter);
		}

		@Override
		protected AdapterWrapper<IPeripheralMethodExecutor> wrapInlineAdapter(Class<?> targetClass) {
			return new PeripheralInlineAdapterWrapper(targetClass);
		}
	};

	public static void addObjectAdapter(IObjectAdapter adapter) {
		objects.addAdapter(adapter);
	}

	public static void addPeripheralAdapter(IPeripheralAdapter adapter) {
		peripherals.addAdapter(adapter);
	}

	public static void addInlinePeripheralAdapter(Class<?> cls) {
		peripherals.addInlineAdapter(cls);
	}

	@SuppressWarnings("unchecked")
	public static void registerPeripherals() {
		Map<Class<? extends TileEntity>, String> classToNameMap = PeripheralUtils.getClassToNameMap();

		Set<Class<? extends TileEntity>> candidates = Sets.newHashSet();

		for (Map.Entry<Class<? extends TileEntity>, String> e : classToNameMap.entrySet()) {
			Class<? extends TileEntity> teClass = e.getKey();

			if (teClass == null) {
				Log.warn("TE with id %s has null key", e.getValue());
			} else if (!IPeripheral.class.isAssignableFrom(teClass)) {
				candidates.add(teClass);
			}
		}

		Set<Class<?>> classesWithAdapters = peripherals.getAllClasses();
		Set<Class<? extends TileEntity>> classesToRegister = Sets.newHashSet();

		for (Class<?> adaptableClass : classesWithAdapters) {
			if (TileEntity.class.isAssignableFrom(adaptableClass)) {
				// no need to continue, since CC does .isAssignableFrom when
				// searching for peripheral
				classesToRegister.add((Class<? extends TileEntity>)adaptableClass);
			} else if (!adaptableClass.isInterface()) {
				Log.warn("Class %s is neither interface nor TileEntity. Skipping peripheral registration.", adaptableClass);
			} else {
				for (Class<? extends TileEntity> teClass : candidates) {
					if (IPeripheralProvider.class.isAssignableFrom(teClass) || adaptableClass.isAssignableFrom(teClass)) {
						classesToRegister.add(teClass);
					}
				}
			}
		}

		Log.info("Registering peripheral handler for %d classes", classesToRegister.size());
		for (Class<? extends TileEntity> teClass : classesToRegister) {
			Log.finer("Adding integration for %s", teClass);
			ComputerCraftAPI.registerExternalPeripheral(teClass, peripheralHandler);
		}
	}

	private final Multimap<Class<?>, AdapterWrapper<E>> externalAdapters = HashMultimap.create();

	private final Map<Class<?>, AdapterWrapper<E>> internalAdapters = Maps.newHashMap();

	private final Map<Class<?>, AdaptedClass<E>> classes = Maps.newHashMap();

	private Set<Class<?>> getAllClasses() {
		return Sets.union(externalAdapters.keySet(), internalAdapters.keySet());
	}

	public void addAdapter(A adapter) {
		final AdapterWrapper<E> wrapper;
		try {
			wrapper = wrapExternalAdapter(adapter);
		} catch (Throwable e) {
			Log.warn(e, "Something went terribly wrong while adding internal adapter '%s'. It will be disabled", adapter.getClass());
			return;
		}
		final Class<?> targetCls = wrapper.targetCls;
		Preconditions.checkArgument(!Object.class.equals(wrapper.targetCls), "Can't add adapter for Object class");

		Log.info("Registering adapter %s for class %s", wrapper.adapterClass, targetCls);
		externalAdapters.put(wrapper.targetCls, wrapper);
	}

	public void addInlineAdapter(Class<?> targetCls) {
		AdapterWrapper<E> wrapper = wrapInlineAdapter(targetCls);

		Log.info("Registering auto-created adapter for class %s", targetCls);
		internalAdapters.put(targetCls, wrapper);
	}

	public AdaptedClass<E> getAdapterClass(Class<?> targetCls) {
		AdaptedClass<E> value = classes.get(targetCls);
		if (value == null) {
			value = adaptClass(targetCls);
			classes.put(targetCls, value);
		}

		return value;
	}

	Collection<AdapterWrapper<E>> getExternalAdapters(Class<?> targetCls) {
		return Collections.unmodifiableCollection(externalAdapters.get(targetCls));
	}

	AdapterWrapper<E> getInlineAdapter(Class<?> targetCls) {
		AdapterWrapper<E> wrapper = internalAdapters.get(targetCls);
		if (wrapper == null) {
			wrapper = wrapInlineAdapter(targetCls);
			internalAdapters.put(targetCls, wrapper);
		}

		return wrapper;
	}

	protected abstract AdaptedClass<E> adaptClass(Class<?> targetClass);

	protected abstract AdapterWrapper<E> wrapExternalAdapter(A adapter);

	protected abstract AdapterWrapper<E> wrapInlineAdapter(Class<?> targetClass);

	public static ILuaObject wrapObject(Object o) {
		return LuaObjectWrapper.wrap(objects, o);
	}

	public static HostedPeripheral createHostedPeripheral(Object target) {
		AdaptedClass<IPeripheralMethodExecutor> adapter = peripherals.adaptClass(target.getClass());
		return new HostedPeripheral(adapter, target);
	}
}

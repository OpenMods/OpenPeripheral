package openperipheral.interfaces.cc.executors;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmods.Log;
import openmods.utils.WorldUtils;
import openmods.world.DelayedActionTickHandler;
import openperipheral.adapter.AdapterLogicException;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.api.adapter.IWorldProvider;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public abstract class SynchronousExecutor<T> extends PeripheralExecutor<T> {

	private static final String SYNC_EVENT = "op_tick_sync";

	private static int currentId;

	private static synchronized int getNextId() {
		return currentId++;
	}

	public boolean isAlwaysSafe() {
		return true;
	}

	public static Object[] wrap(Object... args) {
		return args;
	}

	public static final Object[] DUMMY = new Object[0];

	static class Responder {
		private final ILuaContext context;
		private final IComputerAccess access;
		private boolean nobodyLovesMe;
		private final int transactionId;

		public Throwable error;
		public Object[] result;

		private Responder(ILuaContext context, IComputerAccess access) {
			this.context = context;
			this.access = access;
			transactionId = getNextId();
		}

		public void waitForEvent() throws Exception {
			while (!nobodyLovesMe) {
				Object[] result;
				try {
					result = context.pullEvent(SYNC_EVENT);
				} catch (Exception e) {
					nobodyLovesMe = true;
					throw e;
				} catch (Throwable t) {
					nobodyLovesMe = true;
					throw Throwables.propagate(t);
				}
				int transactionId = ((Number)result[1]).intValue();
				if (transactionId == this.transactionId) break;
			}
		}

		public void signalEvent(boolean log) {
			Preconditions.checkState(error != null || result != null, "Must set either 'error' or 'result' before firing event");
			if (nobodyLovesMe) {
				if (log) Log.warn("Ignoring signal for transaction %s. (sob)", transactionId);
			} else {
				try {
					access.queueEvent(SYNC_EVENT, wrap(transactionId));
				} catch (Exception e) {
					// computer got invalidated, but we get here due to delayed tick
					if (log) Log.warn(e, "Failed to signal response to transaction '%d'", transactionId);
				}
			}
		}
	}

	public abstract boolean isLoaded(T target);

	public abstract World getWorld(T target);

	public static class TileEntityExecutor extends SynchronousExecutor<TileEntity> {

		public static final PeripheralExecutor<?> INSTANCE = new TileEntityExecutor();

		@Override
		public World getWorld(TileEntity target) {
			return target.getWorldObj();
		}

		@Override
		public boolean isLoaded(TileEntity target) {
			return WorldUtils.isTileEntityValid(target);
		}

	}

	public static class WorldProviderExecutor extends SynchronousExecutor<IWorldProvider> {

		public static final PeripheralExecutor<?> INSTANCE = new WorldProviderExecutor();

		@Override
		public World getWorld(IWorldProvider target) {
			return target.getWorld();
		}

		@Override
		public boolean isLoaded(IWorldProvider target) {
			return target.isValid();
		}

	}

	@Override
	public Object[] execute(final IMethodExecutor executor, final T target, final IComputerAccess computer, final ILuaContext context, final Object[] args) throws Exception {
		World world = getWorld(target);
		Preconditions.checkNotNull(world, "Trying to execute OnTick method, but no available world");

		final Responder responder = new Responder(context, computer);

		DelayedActionTickHandler.INSTANCE.addTickCallback(world, new Runnable() {
			@Override
			public void run() {
				boolean isStillLoaded = isLoaded(target);
				if (isStillLoaded) {
					try {
						responder.result = call(executor, target, computer, context, args);
					} catch (Throwable e) {
						responder.error = e;
					}
					responder.signalEvent(true);
				} else {
					// object is unloaded, but we still can try to finish other thread
					responder.result = DUMMY;
					responder.signalEvent(false);
				}
			}
		});

		responder.waitForEvent();

		// This code was executed in main thread, so there are no special exceptions we need to pass
		if (responder.error != null) throw new AdapterLogicException(responder.error);
		return responder.result;
	}
}
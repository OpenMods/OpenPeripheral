package openperipheral.adapter.peripheral;

import java.util.concurrent.Callable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmods.Log;
import openperipheral.TickHandler;
import openperipheral.api.IWorldProvider;
import openperipheral.util.PrettyPrint;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;


public abstract class ExecutionStrategy {
	public abstract Object[] execute(Object target, IComputerAccess computer, ILuaContext context, Callable<Object[]> callable) throws Exception;

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

	private static class Responder {
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
				} catch (Throwable e) {
					// we will usually get here after program termination or
					// computer reset
					nobodyLovesMe = true;
					throw Throwables.propagate(e);
				}
				int transactionId = ((Number)result[1]).intValue();
				if (transactionId == this.transactionId) break;
			}
		}

		public void signalEvent() {
			Preconditions.checkState(error != null || result != null, "Must set either 'error' or 'result' before firing event");
			if (nobodyLovesMe) {
				Log.warn("Ignoring signal for transaction %s. (sob)", transactionId);
			} else {
				try {
					access.queueEvent(SYNC_EVENT, wrap(transactionId));
				} catch (Exception e) {
					// computer got restarted, but we get here due to race
					// condition
					Log.warn(e, "Failed to signal response to transaction '%d'", transactionId);
				}
			}
		}
	}

	private abstract static class OnTick<T> extends ExecutionStrategy {

		public abstract World getWorld(T target);

		@Override
		public Object[] execute(Object target, IComputerAccess computer, ILuaContext context, final Callable<Object[]> callable) throws Exception {
			@SuppressWarnings("unchecked")
			World world = getWorld((T)target);
			Preconditions.checkNotNull(world, "Trying to execute OnTick method, but no available world");

			final Responder responder = new Responder(context, computer);

			TickHandler.addTickCallback(world, new Runnable() {
				@Override
				public void run() {
					try {
						responder.result = callable.call();
					} catch (Throwable e) {
						responder.error = e;
					}
					responder.signalEvent();
				}
			});

			responder.waitForEvent();

			if (responder.error != null) {
				String description = PrettyPrint.getMessageForThrowable(responder.error);
				throw new RuntimeException(description, responder.error);
			}

			return responder.result;
		}
	}

	public static final ExecutionStrategy ASYNCHRONOUS = new ExecutionStrategy() {
		@Override
		public Object[] execute(Object target, IComputerAccess computer, ILuaContext context, Callable<Object[]> callable) throws Exception {
			return callable.call();
		}
	};

	private final static ExecutionStrategy ON_TICK_TILE_ENTITY = new OnTick<TileEntity>() {

		@Override
		public World getWorld(TileEntity target) {
			return target.worldObj;
		}

	};

	private final static ExecutionStrategy ON_TICK_WORLD_PROVIDER = new OnTick<IWorldProvider>() {

		@Override
		public World getWorld(IWorldProvider target) {
			return target.getWorld();
		}

	};

	private final static ExecutionStrategy ON_TICK_OTHER = new OnTick<Object>() {

		@Override
		public World getWorld(Object target) {
			if (target instanceof TileEntity) return ((TileEntity)target).worldObj;
			if (target instanceof IWorldProvider) return ((IWorldProvider)target).getWorld();
			throw new UnsupportedOperationException(String.format("Methods of adapter for %s cannot be synchronous", target.getClass()));
		}

		@Override
		public boolean isAlwaysSafe() {
			return false;
		}
	};

	public static ExecutionStrategy createOnTickStrategy(Class<?> targetClass) {
		if (TileEntity.class.isAssignableFrom(targetClass)) return ON_TICK_TILE_ENTITY;
		else if (IWorldProvider.class.isAssignableFrom(targetClass)) return ON_TICK_WORLD_PROVIDER;
		else return ON_TICK_OTHER;
	}
}

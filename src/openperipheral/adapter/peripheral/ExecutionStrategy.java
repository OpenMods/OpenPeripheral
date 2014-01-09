package openperipheral.adapter.peripheral;

import java.util.Arrays;
import java.util.concurrent.Callable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmods.Log;
import openperipheral.TickHandler;
import openperipheral.api.IWorldProvider;
import openperipheral.util.PrettyPrint;

import com.google.common.base.Preconditions;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;

public abstract class ExecutionStrategy {
	public abstract Object[] execute(Object target, IComputerAccess computer, ILuaContext context, Callable<Object[]> callable) throws Exception;

	public boolean isAlwaysSafe() {
		return true;
	}
	
	public static Object[] wrap(Object... args) {
		return args;
	}

	private static class Responder {
		private final IComputerAccess access;
		private boolean nobodyLovesMe;

		private Responder(IComputerAccess access) {
			this.access = access;
		}

		public synchronized void tooLate() {
			nobodyLovesMe = true;
		}

		public synchronized void queueEvent(String event, Object... args) {
			if (nobodyLovesMe) {
				Log.warn("Ignoring event '%s', args='%s'. (sob)", event, Arrays.toString(args));
			} else {
				try {
					access.queueEvent(event, args);
				} catch (Exception e) {
					Log.warn(e, "Failed to queue response '%s', args='%s'", event, Arrays.toString(args));
				}
			}
		}
	}

	private abstract static class OnTick<T> extends ExecutionStrategy {

		private static final String EVENT_SUCCESS = "openperipheral_success";
		private static final String EVENT_ERROR = "openperipheral_error";

		public abstract World getWorld(T target);

		@Override
		public Object[] execute(Object target, IComputerAccess computer, ILuaContext context, final Callable<Object[]> callable) throws Exception {
			@SuppressWarnings("unchecked")
			World world = getWorld((T)target);
			Preconditions.checkNotNull(world, "Trying to execute OnTick method, but no available world");

			final Responder responder = new Responder(computer);
			TickHandler.addTickCallback(world, new Runnable() {
				@Override
				public void run() {
					// on the tick, we execute the method, format the response,
					// then stick it into an event
					try {
						Object[] response = callable.call();
						responder.queueEvent(EVENT_SUCCESS, response);
					} catch (Throwable e) {
						responder.queueEvent(EVENT_ERROR, wrap(PrettyPrint.getMessageForThrowable(e)));
					}
				}
			});

			// while we don't have an OpenPeripheral event
			while (true) {
				try {
					Object[] event = context.pullEvent(null);
					String eventName = (String)event[0];
					if (eventName.equals(EVENT_ERROR)) throw new RuntimeException((String)event[1]);
					else if (eventName.equals(EVENT_SUCCESS)) return Arrays.copyOfRange(event, 1, event.length);
				} catch (InterruptedException e) {
					responder.tooLate();
					throw e;
				}
			}
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

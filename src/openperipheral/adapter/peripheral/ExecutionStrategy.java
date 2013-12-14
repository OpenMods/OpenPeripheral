package openperipheral.adapter.peripheral;

import java.util.Arrays;
import java.util.concurrent.Callable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openperipheral.TickHandler;
import openperipheral.api.IWorldProvider;
import openperipheral.util.PrettyPrint;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;

public abstract class ExecutionStrategy {
	public abstract Object[] execute(Object target, IComputerAccess computer, ILuaContext context, Callable<Object[]> callable) throws Exception;

	public boolean isAlwaysSafe() {
		return true;
	}

	private abstract static class OnTick<T> extends ExecutionStrategy {

		private static final String EVENT_SUCCESS = "openperipheral_success";
		private static final String EVENT_ERROR = "openperipheral_error";

		public abstract World getWorld(T target);

		@Override
		public Object[] execute(Object target, final IComputerAccess computer, ILuaContext context, final Callable<Object[]> callable) throws Exception {
			@SuppressWarnings("unchecked")
			World world = getWorld((T)target);
			Preconditions.checkNotNull(world, "Trying to execute OnTick method, but no available world");
			TickHandler.addTickCallback(world, new Runnable() {
				@Override
				public void run() {
					// on the tick, we execute the method, format the response,
					// then stick it into an event
					try {
						Object[] response = callable.call();
						computer.queueEvent(EVENT_SUCCESS, response);
					} catch (Throwable e) {
						computer.queueEvent(EVENT_ERROR, ArrayUtils.toArray(PrettyPrint.getMessageForThrowable(e)));
					}
				}
			});

			// while we don't have an OpenPeripheral event
			while (true) {
				Object[] event = context.pullEvent(null);
				String eventName = (String)event[0];
				if (eventName.equals(EVENT_ERROR)) throw new RuntimeException((String)event[1]);
				else if (eventName.equals(EVENT_SUCCESS)) return Arrays.copyOfRange(event, 1, event.length);
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

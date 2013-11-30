package openperipheral;

import java.util.EnumSet;
import java.util.Map;
import java.util.Queue;

import net.minecraft.world.World;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler {

	private static Map<Integer, Queue<Runnable>> callbacks = Maps.newHashMap();

	private static Queue<Runnable> getWorldQueue(int worldId) {
		synchronized (callbacks) {
			Queue<Runnable> result = callbacks.get(worldId);

			if (result == null) {
				result = Queues.newConcurrentLinkedQueue();
				callbacks.put(worldId, result);
			}

			return result;
		}
	}

	public static void addTickCallback(World world, Runnable callback) throws InterruptedException {
		int worldId = world.provider.dimensionId;
		getWorldQueue(worldId).add(callback);
	}

	@Override
	public String getLabel() {
		return "OpenPeripheralCore";
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickObjects) {
		if (type.contains(TickType.WORLD)) {
			World world = (World)tickObjects[0];
			if (!world.isRemote) {
				int worldId = world.provider.dimensionId;
				Queue<Runnable> callbacks = getWorldQueue(worldId);

				Runnable callback;
				while ((callback = callbacks.poll()) != null) {
					callback.run();
				}
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {}

}

package openperipheral;

import java.util.Map;
import java.util.Queue;

import net.minecraft.world.World;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public class DelayedActionTickHandler {

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

	public static void addTickCallback(World world, Runnable callback) {
		int worldId = world.provider.dimensionId;
		getWorldQueue(worldId).add(callback);
	}

	public void onWorldTick(WorldTickEvent evt) {
		if (evt.side == Side.SERVER && evt.phase == Phase.END) {
			int worldId = evt.world.provider.dimensionId;
			Queue<Runnable> callbacks = getWorldQueue(worldId);

			Runnable callback;
			while ((callback = callbacks.poll()) != null) {
				callback.run();
			}
		}
	}
}

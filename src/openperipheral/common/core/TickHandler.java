package openperipheral.common.core;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openperipheral.common.tileentity.TileEntityGlassesBridge;
import openperipheral.common.util.MiscUtils;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler {
	
	private static Map<Integer, LinkedBlockingQueue<FutureTask>> callbacks = Collections.synchronizedMap(new HashMap<Integer, LinkedBlockingQueue<FutureTask>>());

	public static Future addTickCallback(World world, Callable callback) throws InterruptedException {
		int worldId = world.provider.dimensionId;
		if (!callbacks.containsKey(Integer.valueOf(worldId))) {
			callbacks.put(worldId, new LinkedBlockingQueue());
		}
		FutureTask task = new FutureTask(callback);
		callbacks.get(worldId).put(task);
		return task;
	}

	@Override
	public String getLabel() {
		return "OpenPeripheral";
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickObjects) {

		
		if (type.contains(TickType.WORLD)) {

			World world = (World) tickObjects[0];
			if (!world.isRemote) {
				for (EntityPlayer player : (List<EntityPlayer>) world.playerEntities) {
					ItemStack helmet = player.inventory.armorItemInSlot(3);
					if (MiscUtils.canBeGlasses(helmet)) {
						TileEntityGlassesBridge bridge = TileEntityGlassesBridge.getGlassesBridgeFromStack(player.worldObj, helmet);
						if (bridge != null) {
							bridge.registerPlayer(player);
						}
					}
				}
			}

			int worldId = world.provider.dimensionId;
			if (callbacks.containsKey(worldId)) {
				LinkedBlockingQueue<FutureTask> callbackList = callbacks.get(worldId);
				FutureTask callback = callbackList.poll();
				while (callback != null) {
					callback.run();
					callback = callbackList.poll();
				}
			}
		}
	}

}

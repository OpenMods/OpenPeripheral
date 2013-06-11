package openperipheral.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import openperipheral.common.tileentity.TileEntityGlassesBridge;
import openperipheral.common.util.MiscUtils;

public class ChatCommandInterceptor {

	@ForgeSubscribe
	public void onServerChatEvent(ServerChatEvent event) {
		EntityPlayerMP player = event.player;
		if (player != null) {
			if (event.message.startsWith("$$")) {
				ItemStack headSlot = player.inventory.armorItemInSlot(3);

				if (headSlot != null && MiscUtils.canBeGlasses(headSlot)) {

					event.setCanceled(true);

					TileEntityGlassesBridge te = TileEntityGlassesBridge.getGlassesBridgeFromStack(event.player.worldObj, headSlot);

					if (te != null) {
						te.onChatCommand(event.message.substring(2).trim(), event.username);
					}
				}
			}
		}
	}
}

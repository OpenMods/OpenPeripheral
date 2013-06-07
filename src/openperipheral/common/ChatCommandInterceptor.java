package openperipheral.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import openperipheral.OpenPeripheral;
import openperipheral.common.tileentity.TileEntityGlassesBridge;

public class ChatCommandInterceptor {

	@ForgeSubscribe
	public void onServerChatEvent(ServerChatEvent event) {
		EntityPlayerMP player = event.player;
		if (player != null) {
			if (event.message.startsWith("$$")) {
				ItemStack headSlot = player.inventory.armorItemInSlot(3);
				if (headSlot != null && headSlot.getItem() == OpenPeripheral.Items.glasses) {
					event.setCanceled(true);
					
					if (!headSlot.hasTagCompound()) {
						return;
					}
					
					NBTTagCompound tag = headSlot.getTagCompound();
					if (!tag.hasKey("guid")) {
						return;
					}
					
					int x = tag.getInteger("x");
					int y = tag.getInteger("y");
					int z = tag.getInteger("z");
					int d = tag.getInteger("d");
					
					if (player.worldObj.provider.dimensionId == d) {
						if (player.worldObj.blockExists(x, y, z)) {
							TileEntity te = player.worldObj.getBlockTileEntity(x, y, z);
							if (te instanceof TileEntityGlassesBridge) {
								((TileEntityGlassesBridge) te).onChatCommand(
										event.message.substring(2).trim()
								);
							}
						}
					}
				}
			}
		}
	}
}

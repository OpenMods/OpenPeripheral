package openperipheral.common.terminal;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import openperipheral.OpenPeripheral;
import openperipheral.common.tileentity.TileEntityGlassesBridge;

public class DrawableManager {

	public static final int DELETE = 0;
	public static final int CHANGE = 1;
	public String currentGuid = null;
	ArrayList<IDrawable> drawableList = new ArrayList<IDrawable>();
	
	
	Comparator<IDrawable> zIndexComparator = new Comparator<IDrawable>() {
        @Override public int compare(IDrawable s1, IDrawable s2) {
            return s1.getZIndex() - s2.getZIndex();
        }           
    };
	
	@ForgeSubscribe
	public void onWorldLoad(Load loadEvent) {
		drawables.clear();
	}
	
	@ForgeSubscribe
	public void onServerChatEvent(ServerChatEvent event) {
		EntityPlayerMP player = event.player;
		if (player != null) {
			if (event.message.startsWith("$$")) {
				event.setCanceled(true);
				ItemStack headSlot = player.inventory.armorItemInSlot(3);
				if (headSlot != null && headSlot.getItem() == OpenPeripheral.Items.glasses) {
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
								((TileEntityGlassesBridge)te).onChatCommand(event.message.substring(2).trim());
							}
						}
					}
				}
			}
		}
	}
	
	private HashMap<Integer, IDrawable> drawables = new HashMap<Integer, IDrawable>();
	
	public Collection<IDrawable> getDrawables() {
		return drawableList;
	}

	public void handlePacket(Packet250CustomPayload packet) {
		
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        
        try {
        		String guid = inputStream.readUTF();
        		if (currentGuid == null || !currentGuid.equals(guid)) {
        			drawables.clear();
        			currentGuid = guid;
        		}
                int drawableCount = inputStream.readInt();
                for (int i = 0; i < drawableCount; i++) {
                	byte changeType = inputStream.readByte();
                	int drawableId = inputStream.readInt();
                	switch(changeType) {
                	case DELETE:
            			drawables.remove(drawableId);
            			break;
                	case CHANGE:
                    	byte drawableType = inputStream.readByte();
                    	IDrawable drawable = null;
                		if (drawables.containsKey(drawableId)) {
                			drawable = drawables.get(drawableId);
                		}else {
                			switch(drawableType) {
                			case 0:
                				drawable = new DrawableText();
                				break;
                			default:
                				drawable = new DrawableBox();
                			}
                		}
            			if (drawable != null) {
            				drawable.readFrom(inputStream);
            				drawables.put(drawableId, drawable);
            			}
                	}
                }

        		drawableList.clear();
        		drawableList.addAll(drawables.values());
        		Collections.sort(drawableList, zIndexComparator);
                
        } catch (IOException e) {
                e.printStackTrace();
                return;
        }
        
	}

}

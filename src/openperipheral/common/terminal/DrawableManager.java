package openperipheral.common.terminal;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import openperipheral.OpenPeripheral;
import openperipheral.common.tileentity.TileEntityGlassesBridge;
import openperipheral.common.util.ByteUtils;
import openperipheral.common.util.PacketChunker;

public class DrawableManager {

	ArrayList<IDrawable> drawableList = new ArrayList<IDrawable>();

	public static final byte CLEAR_ALL_FLAG = 0;
	public static final byte CHANGE_FLAG = 1;
	
	private int displayList = 0;
	
	public DrawableManager() {
		
	}
	
	@ForgeSubscribe
	public void onRenderGameOverlay(RenderGameOverlayEvent evt) {
		for (IDrawable drawable : drawableList) {
			drawable.draw(evt.partialTicks, evt.mouseX, evt.mouseY);
		}
	}

	private HashMap<Short, IDrawable> drawables = new HashMap<Short, IDrawable>();
	
	Comparator<IDrawable> zIndexComparator = new Comparator<IDrawable>() {
		@Override
		public int compare(IDrawable s1, IDrawable s2) {
			return s1.getZIndex() - s2.getZIndex();
		}
	};

	@ForgeSubscribe
	public void onWorldLoad(Load loadEvent) {
		drawables.clear();
	}

	public Collection<IDrawable> getDrawables() {
		return drawableList;
	}

	public void handlePacket(Packet250CustomPayload packet) {
		
		try {
			
			byte[] bytes = PacketChunker.instance.getBytes(packet);
			
			if (bytes == null) {
				return;
			}

			DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(bytes));

			byte type = inputStream.readByte();
			
			if (type == 0) {
				drawables.clear();
				drawableList.clear();
				return;
			}
			
			// how many drawable objects are in this packet
			short drawableCount = inputStream.readShort();
			
			for (int i = 0; i < drawableCount; i++) {
				
				// the change mask specifies which properties have changed
				short changeMask = inputStream.readShort();
				
				// get the id for this drawable object
				short drawableId = inputStream.readShort();
				
				// if slot 0 is false, it means we need to remove the object
				if (!ByteUtils.get(changeMask, 0)) {
					drawables.remove(drawableId);
				}else {
					// drawable type means text/box
					byte drawableType = inputStream.readByte();
					IDrawable drawable = null;
					if (drawables.containsKey(drawableId)) {
						drawable = drawables.get(drawableId);
					} else {
						switch (drawableType) {
						case 0:
							drawable = new DrawableText();
							break;
						default:
							drawable = new DrawableBox();
						}
					}
					if (drawable != null) {
						drawable.readFrom(inputStream, changeMask);
						drawables.put(drawableId, drawable);
					}
				}
			}

			drawableList.clear();
			drawableList.addAll(drawables.values());
			Collections.sort(drawableList, zIndexComparator);
			
		}catch(Exception e) {
			
		}

	}

}

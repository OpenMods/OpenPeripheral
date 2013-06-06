package openperipheral.common.terminal;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

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
import openperipheral.common.util.ByteUtils;

public class DrawableManager {

	public static final int DELETE = 0;
	public static final int CHANGE = 1;
	ArrayList<IDrawable> drawableList = new ArrayList<IDrawable>();
	private int length = 0;
	private int remainingLength = 0;
	private HashMap<Integer, byte[][]> packetStack = new HashMap<Integer, byte[][]>();

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

	@ForgeSubscribe
	public void onServerChatEvent(ServerChatEvent event) {
		EntityPlayerMP player = event.player;
		if (player != null) {
			if (event.message.startsWith("$$")) {
				ItemStack headSlot = player.inventory.armorItemInSlot(3);
				if (headSlot != null
						&& headSlot.getItem() == OpenPeripheral.Items.glasses) {
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
							TileEntity te = player.worldObj.getBlockTileEntity(
									x, y, z);
							if (te instanceof TileEntityGlassesBridge) {
								((TileEntityGlassesBridge) te)
								.onChatCommand(event.message.substring(
										2).trim());
							}
						}
					}
				}
			}
		}
	}


	public Collection<IDrawable> getDrawables() {
		return drawableList;
	}

	public void handlePacket(Packet250CustomPayload packet) {

		DataInputStream inputStream1 = new DataInputStream(new ByteArrayInputStream(packet.data));
		try {
			
			short chunkLength = inputStream1.readShort();
			short chunkIndex = inputStream1.readShort();
			int packetId = inputStream1.readInt();

			if (!packetStack.containsKey(packetId)) {
				packetStack.put(packetId, new byte[chunkLength][]);
			}

			byte[][] stack = packetStack.get(packetId);

			byte[] remainingBytes = new byte[packet.data.length - 8];
			inputStream1.read(remainingBytes, 0, remainingBytes.length);
			stack[chunkIndex] = remainingBytes;

			int chunksLeft = 0;
			for (byte[] s : stack) {
				if (s == null) {
					chunksLeft++;
				}
			}

			if (chunksLeft == 0) {

				int totalLength =  0;
				for (byte[] s : stack) {
					totalLength += s.length;
				}

				byte[] fullPacket = new byte[totalLength];
				int offset = 0;
				for (short i = 0; i < chunkLength; i++) {
					byte[] chunkPart = stack[i];
					System.arraycopy(chunkPart, 0, fullPacket, offset, chunkPart.length);
					offset += chunkPart.length;
				}

				packetStack.remove(packetId);
				
				DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(fullPacket));

				byte type = inputStream.readByte();
				
				if (type == 0) {
					drawables.clear();
					return;
				}
				
				short drawableCount = inputStream.readShort();
				for (int i = 0; i < drawableCount; i++) {
					short changeMask = inputStream.readShort();
					short drawableId = inputStream.readShort();
					
					if (!ByteUtils.get(changeMask, 0)) {
						drawables.remove(drawableId);
					}else {
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
			}

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

	}

}

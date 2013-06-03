package openperipheral.common.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import openperipheral.OpenPeripheral;
import openperipheral.common.terminal.DrawableBox;
import openperipheral.common.terminal.DrawableText;
import openperipheral.common.terminal.IDrawable;
import openperipheral.common.util.StringUtils;
import dan200.computer.core.ILuaObject;

public class TileEntityGlassesBridge extends TileEntity {

	public HashMap<Integer, IDrawable> drawables = new HashMap<Integer, IDrawable>();
	public ArrayList<Integer> changes = new ArrayList<Integer>();
	public ArrayList<String> players = new ArrayList<String>();
	public ArrayList<String> newPlayers = new ArrayList<String>();
	public ArrayList<Integer> deletes = new ArrayList<Integer>();

	private String guid = StringUtils.randomString(8);
	private int count = 1;

	public TileEntityGlassesBridge() {
		
	}
	
	public int getKeyForDrawable(IDrawable d) {
		for (Entry<Integer, IDrawable> entry : drawables.entrySet()) {
			if (entry.getValue().equals(d)) {
				return entry.getKey();
			}
		}
		return -1;
	}
	
	public void setDeleted(IDrawable d) {
		int key = getKeyForDrawable(d);
		drawables.remove(key);
		changes.remove(d);
		deletes.add(key);
	}
	
	public void markChanged(IDrawable d) {
		int key = getKeyForDrawable(d);
		if (key != -1) {
			changes.add(key);
		}
	}
	
	public void registerPlayer(EntityPlayer player) {
		if (!players.contains(player.username) && !newPlayers.contains(player.username)) {
			newPlayers.add(player.username);
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			if (newPlayers.size() > 0) {
				Packet fullPacket = createFullPacket();
				for (String playerName: newPlayers) {
					EntityPlayer player = worldObj.getPlayerEntityByName(playerName);
					if (player != null){
						((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(fullPacket);
					}
				}
			}
			if (players.size() > 0) {
				Packet changePacket = null;
				if (changes.size() > 0 || deletes.size() > 0) {
					changePacket = createChangePacket();
				}
				Iterator<String> iter = players.iterator();
				while (iter.hasNext()) {
					String playerName = iter.next();
					EntityPlayer player = worldObj.getPlayerEntityByName(playerName);
					if (player == null || !isPlayerValid(player)) {
						iter.remove();
					}else {
						if (changePacket != null) {
							((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(changePacket);
						}
					}
				}
			}
			changes.clear();
			deletes.clear();
			for (String newPlayer : newPlayers) {
				if (!players.contains(newPlayer)) {
					players.add(newPlayer);
				}
			}
			newPlayers.clear();
		}
	}

	private boolean isPlayerValid(EntityPlayer player) {
		ItemStack glasses = player.inventory.armorItemInSlot(3);
		if (glasses == null) {
			return false;
		}
		Item item = glasses.getItem();
		if (item != OpenPeripheral.Items.glasses) {
			return false;
		}
		if (!glasses.hasTagCompound()) {
			return false;
		}
		NBTTagCompound tag = glasses.getTagCompound();
		if (!tag.hasKey("guid")) {
			return false;
		}
		return tag.getString("guid").equals(guid);
	}

	private Packet createFullPacket() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeUTF(getGuid());
			outputStream.writeInt(changes.size());
			for (Entry<Integer, IDrawable> entries : drawables.entrySet()) {
				int changeId = entries.getKey();
				IDrawable drawable = entries.getValue();
				outputStream.writeByte(1);
				outputStream.writeInt(changeId);
				if (drawable instanceof DrawableText) {
					outputStream.writeByte((byte)0);
				}else {
					outputStream.writeByte((byte)1);
				}
				drawable.writeTo(outputStream);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "OpenPeripheral";
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		return packet;

	}

	private Packet createChangePacket() {

		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeUTF(getGuid());
			outputStream.writeInt(changes.size() + deletes.size());
			for (int changeId : changes) {

				IDrawable drawable = drawables.get(changeId);
				outputStream.writeByte(1);
				outputStream.writeInt(changeId);
				if (drawable instanceof DrawableText) {
					outputStream.writeByte((byte)0);
				}else {
					outputStream.writeByte((byte)1);
				}
				drawable.writeTo(outputStream);
			}
			
			for (int delete : deletes) {
				outputStream.writeByte(0);
				outputStream.writeInt(delete);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "OpenPeripheral";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		return packet;
	}

	public synchronized ILuaObject addText(int x, int y, String text, int color) {
		drawables.put(count, new DrawableText(this, x, y, text, color));
		changes.add(count);
		return (ILuaObject)drawables.get(count++);
	}
	
	public synchronized ILuaObject addBox(int x, int y, int width, int height, int color, double alpha) {
		drawables.put(count, new DrawableBox(this, x, y, width, height, color, alpha));
		changes.add(count);
		return (ILuaObject)drawables.get(count++);
	}

	public synchronized ILuaObject getById(int id) {
		return (ILuaObject)drawables.get(id);
	}
	
	public synchronized HashMap getAllIds() {
		HashMap all = new HashMap();
		int i = 1;
		for (Integer id : drawables.keySet()) {
			all.put(i++, id);
		}
		return all;
	}
	
	public synchronized void clear() {
		deletes.clear();
		deletes.addAll(drawables.keySet());
		drawables.clear();
		changes.clear();
	}

	public String getGuid() {
		return guid;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setString("guid", guid);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		guid = tag.getString("guid");
	}


}

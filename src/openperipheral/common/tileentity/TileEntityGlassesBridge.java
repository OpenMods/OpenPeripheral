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
import openperipheral.IAttachable;
import openperipheral.OpenPeripheral;
import openperipheral.common.terminal.DrawableBox;
import openperipheral.common.terminal.DrawableText;
import openperipheral.common.terminal.IDrawable;
import openperipheral.common.util.StringUtils;
import openperipheral.common.util.ThreadLock;
import dan200.computer.api.IComputerAccess;
import dan200.computer.core.ILuaObject;

public class TileEntityGlassesBridge extends TileEntity implements IAttachable {

	public HashMap<Integer, IDrawable> drawables = new  HashMap<Integer, IDrawable>();
	public HashMap<Integer, Boolean> changes = new HashMap<Integer, Boolean>();
	public ArrayList<String> players = new ArrayList<String>();
	public ArrayList<String> newPlayers = new ArrayList<String>();
	private ArrayList<IComputerAccess> computers = new ArrayList<IComputerAccess>();
	private ThreadLock lock = new ThreadLock();

	private String guid = StringUtils.randomString(8);
	private int count = 1;

	public TileEntityGlassesBridge() {
	}
	
	public int getKeyForDrawable(IDrawable d) {
		int rtn = -1;
		try {
			lock.lock();
			try {
				for (Entry<Integer, IDrawable> entry : drawables.entrySet()) {
					if (entry.getValue().equals(d)) {
						rtn = entry.getKey();
					}
				}
			} finally {
				lock.unlock();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return rtn;
	}
	
	public void setDeleted(IDrawable d) {
		try {
			lock.lock();
			try {
				int key = getKeyForDrawable(d);
				drawables.remove(key);
				changes.put(key, false);
			} finally {
				lock.unlock();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void markChanged(IDrawable d) {
		try {
			lock.lock();
			try {
				int key = getKeyForDrawable(d);
				if (key != -1) {
					changes.put(key, true);
				}
			} finally {
				lock.unlock();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
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
			try {
				lock.lock();
				try {
					if (newPlayers.size() > 0 && drawables.size() > 0) {
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
						if (changes.size() > 0) {
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
					for (String newPlayer : newPlayers) {
						if (!players.contains(newPlayer)) {
							players.add(newPlayer);
						}
					}
					newPlayers.clear();
				} finally {
					lock.unlock();
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
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
			lock.lock();
			try {
				outputStream.writeInt(drawables.size());
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
			} finally {
				lock.unlock();
			}
		} catch(Exception ex) {
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
			lock.lock();
			try {
				outputStream.writeUTF(getGuid());
				outputStream.writeInt(changes.size());
				for (Entry<Integer,Boolean> change : changes.entrySet()) {
					int changeId = change.getKey();
					IDrawable drawable = drawables.get(changeId);
					if (change.getValue()) {
						outputStream.writeByte(1);
						outputStream.writeInt(changeId);
						if (drawable instanceof DrawableText) {
							outputStream.writeByte((byte)0);
						}else {
							outputStream.writeByte((byte)1);
						}
						drawable.writeTo(outputStream);
					}else {
						outputStream.writeByte(0);
						outputStream.writeInt(changeId);
					}
				}
			} finally {
				lock.unlock();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "OpenPeripheral";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		return packet;
	}

	public ILuaObject addText(int x, int y, String text, int color) {
		ILuaObject obj = null;
		try {
			lock.lock();
			try {
				drawables.put(count, new DrawableText(this, x, y, text, color));
				changes.put(count, true);
				obj = drawables.get(count++);
			} finally {
				lock.unlock();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return (ILuaObject) obj;
	}
	
	public ILuaObject addBox(int x, int y, int width, int height, int color, double alpha) throws InterruptedException {
		ILuaObject obj = null;

		try {
			lock.lock();
			try {
				drawables.put(count, new DrawableBox(this, x, y, width, height, color, alpha));
				changes.put(count, true);
				obj = drawables.get(count++);
			} finally {
				lock.unlock();
			}
		} catch(Exception ex) {
			
		}
		return (ILuaObject)obj;
	}

	public ILuaObject getById(int id) {
		synchronized(lock) {
			return (ILuaObject)drawables.get(id);
		}
	}
	
	public HashMap getAllIds() {
		HashMap all = new HashMap();
		synchronized(lock) {
			int i = 1;
			for (Integer id : drawables.keySet()) {
				all.put(i++, id);
			}	
		}
		return all;
			
	}
	
	public synchronized void clear() {
		try {
			lock.lock();
			try {
				for (Integer key : drawables.keySet()) {
					changes.put(key, false);
				}
				drawables.clear();
			} finally {
				lock.unlock();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
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


	public void onChatCommand(String command) {
		for (IComputerAccess computer : computers) {
			computer.queueEvent("chat_command", new Object[] { command });
		}
	}

	@Override
	public void addComputer(IComputerAccess computer) {
		if (!computers.contains(computer)) {
			computers.add(computer);
		}
	}

	@Override
	public void removeComputer(IComputerAccess computer) {
		computers.remove(computer);
	}

}

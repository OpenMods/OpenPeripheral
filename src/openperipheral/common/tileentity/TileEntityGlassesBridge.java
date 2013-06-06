package openperipheral.common.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
import openperipheral.common.util.ByteUtils;
import openperipheral.common.util.StringUtils;
import openperipheral.common.util.ThreadLock;
import dan200.computer.api.IComputerAccess;
import dan200.computer.core.ILuaObject;

public class TileEntityGlassesBridge extends TileEntity implements IAttachable {

	public HashMap<Short, IDrawable> drawables = new HashMap<Short, IDrawable>();
	public HashMap<Short, Short> changes = new HashMap<Short, Short>();
	public ArrayList<String> players = new ArrayList<String>();
	public ArrayList<String> newPlayers = new ArrayList<String>();
	private ArrayList<IComputerAccess> computers = new ArrayList<IComputerAccess>();
	private ThreadLock lock = new ThreadLock();

	private String guid = StringUtils.randomString(8);
	private short count = 1;
	private int packetId = 0;
	public TileEntityGlassesBridge() {
	}

	public Short getKeyForDrawable(IDrawable d) {
		Short rtn = -1;
		try {
			lock.lock();
			try {
				for (Entry<Short, IDrawable> entry : drawables.entrySet()) {
					if (entry.getValue().equals(d)) {
						rtn = entry.getKey();
					}
				}
			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rtn;
	}

	public void setDeleted(IDrawable d) {
		try {
			lock.lock();
			try {
				Short key = getKeyForDrawable(d);
				if (key != -1) {
					changes.put(key, (short)0);
					drawables.remove(key);
				}
			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void markChanged(IDrawable d, int slot) {
		if (slot == -1) {
			return;
		}
		try {
			lock.lock();
			try {
				Short key = getKeyForDrawable(d);
				if (key != -1) {
					Short current = changes.get(key);

					if (current == null) {
						current = 0;
					}
					current = ByteUtils.set(current, slot, true);
					current = ByteUtils.set(current, 0, true);
					changes.put(key, current);
				}
			}catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void registerPlayer(EntityPlayer player) {
		if (!players.contains(player.username)
				&& !newPlayers.contains(player.username)) {
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
					if (newPlayers.size() > 0) {
						Packet[] fullPackets = createFullPackets();
						for (Packet packet : fullPackets) {
							for (String playerName : newPlayers) {
								EntityPlayer player = worldObj
										.getPlayerEntityByName(playerName);
								if (player != null) {
									((EntityPlayerMP) player).playerNetServerHandler
									.sendPacketToPlayer(packet);
								}
							}
						}
					}
					if (players.size() > 0) {
						Packet[] changePackets = null;
						if (changes.size() > 0) {
							changePackets = createChangePackets();
						}
						Iterator<String> iter = players.iterator();
						while (iter.hasNext()) {
							String playerName = iter.next();
							EntityPlayer player = worldObj.getPlayerEntityByName(playerName);
							if (player == null || !isPlayerValid(player)) {
								iter.remove();
								if (player != null) {
									sendClearScreenToPlayer(player);
								}
							} else {
								if (changePackets != null && changePackets.length > 0) {
									for (Packet packet : changePackets) {
										((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(packet);
									}
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
				}catch(Exception e2) {
					e2.printStackTrace();
				} finally {
					lock.unlock();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void sendClearScreenToPlayer(EntityPlayer player) {
		try {
			if (player instanceof EntityPlayerMP) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
				DataOutputStream outputStream = new DataOutputStream(bos);
				outputStream.writeByte(0);
				byte[] data = bos.toByteArray();
				Packet[] packets = chunkIntoPackets(data);
				for (Packet p : packets) {
					((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(p);
				}
			}
		} catch (IOException e) {
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

	private Packet[] createFullPackets() {

		Packet[] packets = null;

		try {
			lock.lock();
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
				DataOutputStream outputStream = new DataOutputStream(bos);
				outputStream.writeByte(1);
				outputStream.writeShort((short)drawables.size());
				for (Entry<Short, IDrawable> entries : drawables.entrySet()) {
					Short changeId = entries.getKey();
					IDrawable drawable = entries.getValue();
					outputStream.writeShort(Short.MAX_VALUE);
					outputStream.writeShort(changeId);
					if (drawable instanceof DrawableText) {
						outputStream.writeByte((byte) 0);
					} else {
						outputStream.writeByte((byte) 1);
					}
					drawable.writeTo(outputStream, Short.MAX_VALUE);
				}

				byte[] data = bos.toByteArray();

				packets = chunkIntoPackets(data);
			}catch(Exception e2) {
				e2.printStackTrace();

			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return packets;
	}

	private Packet[] createChangePackets() {

		Packet[] packets = null;

		try {
			lock.lock();
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
				DataOutputStream outputStream = new DataOutputStream(bos);
				outputStream.writeByte(1);
				outputStream.writeShort((short)changes.size());
				for (Entry<Short, Short> change : changes.entrySet()) {
					Short changeId = change.getKey();
					IDrawable drawable = drawables.get(changeId);
					outputStream.writeShort(change.getValue());
					outputStream.writeShort(changeId);
					if (ByteUtils.get(change.getValue(), 0)) { // if its not deleted
						if (drawable instanceof DrawableText) {
							outputStream.writeByte((byte) 0);
						} else {
							outputStream.writeByte((byte) 1);
						}
						drawable.writeTo(outputStream, change.getValue());
					}
				}

				byte[] data = bos.toByteArray();

				packets = chunkIntoPackets(data);

				changes.clear();
			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
		}
		return packets;
	}

	public Packet[] chunkIntoPackets(byte[] data) throws IOException {

		byte[] chunk = null;
		int start = 0;
		int chunksize = Short.MAX_VALUE - 100;
		short numChunks = (short)Math.ceil(data.length / (double)chunksize);
		Packet[] packets = new Packet[numChunks];

		for(short i = 0; i < numChunks; i++) {
			byte[] meta = new byte[] {
					(byte)(numChunks >>> 8),
					(byte)numChunks,
					(byte)(i >>> 8),
					(byte)i,
					(byte)(packetId >>> 24),
					(byte)(packetId >>> 16),
					(byte)(packetId >>> 8),
					(byte)packetId
			};

			if(start + chunksize > data.length) {
				chunk = new byte[(data.length - start) + 8];
				System.arraycopy(data, start, chunk, 8, data.length - start);
			} else {
				chunk = new byte[chunksize + 8];
				System.arraycopy(data, start, chunk, 8, chunksize);
			}
			System.arraycopy(meta, 0, chunk, 0, 8);

			Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = "OpenPeripheral";
			packet.data = chunk;
			packet.length = chunk.length;
			packets[i] = packet;
			start += chunksize;
		}
		packetId++;
		return packets;
	}

	public ILuaObject addText(int x, int y, String text, int color) {
		ILuaObject obj = null;
		try {
			lock.lock();
			try {
				drawables.put(count, new DrawableText(this, x, y, text, color));
				changes.put(count, Short.MAX_VALUE);
				obj = drawables.get(count++);
			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return (ILuaObject) obj;
	}

	public ILuaObject addBox(int x, int y, int width, int height, int color,
			double alpha) throws InterruptedException {
		return addGradientBox(x, y, width, height, color, alpha, color, alpha);
	}
	
	public ILuaObject addGradientBox(int x, int y, int width, int height, int color, double alpha, int color2, double alpha2) throws InterruptedException {
		ILuaObject obj = null;

		try {
			lock.lock();
			try {
				drawables.put(count, new DrawableBox(this, x, y, width, height, color, alpha, color2, alpha2));
				changes.put(count, Short.MAX_VALUE);
				obj = drawables.get(count++);
			}catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return (ILuaObject) obj;
	}

	public ILuaObject getById(int id) {
		synchronized (lock) {
			return (ILuaObject) drawables.get(id);
		}
	}

	public HashMap getAllIds() {
		HashMap all = new HashMap();
		synchronized (lock) {
			int i = 1;
			for (Short id : drawables.keySet()) {
				all.put(i++, id);
			}
		}
		return all;

	}

	public synchronized void clear() {
		try {
			lock.lock();
			try {
				for (Short key : drawables.keySet()) {
					changes.put(key, (short)0);
				}
				drawables.clear();
			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
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

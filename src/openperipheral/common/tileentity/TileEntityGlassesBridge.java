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
import net.minecraft.world.World;
import openperipheral.IAttachable;
import openperipheral.OpenPeripheral;
import openperipheral.common.terminal.DrawableBox;
import openperipheral.common.terminal.DrawableManager;
import openperipheral.common.terminal.DrawableText;
import openperipheral.common.terminal.IDrawable;
import openperipheral.common.util.ByteUtils;
import openperipheral.common.util.MiscUtils;
import openperipheral.common.util.PacketChunker;
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
								EntityPlayer player = worldObj.getPlayerEntityByName(playerName);
								if (player != null) {
									((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(packet);
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
				outputStream.writeByte(DrawableManager.CLEAR_ALL_FLAG);
				byte[] data = bos.toByteArray();
				Packet[] packets = PacketChunker.instance.createPackets(data);
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
		if (!MiscUtils.canBeGlasses(glasses)) {
			return false;
		}
		if (!glasses.hasTagCompound()) {
			return false;
		}
		NBTTagCompound tag = glasses.getTagCompound();
		if (!tag.hasKey("openp")) {
			return false;
		}
		NBTTagCompound openPTag = tag.getCompoundTag("openp");
		if (!openPTag.hasKey("guid")) {
			return false;
		}
		return openPTag.getString("guid").equals(guid);
	}

	private Packet[] createFullPackets() {

		Packet[] packets = null;

		try {
			lock.lock();
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
				DataOutputStream outputStream = new DataOutputStream(bos);
				
				outputStream.writeByte(DrawableManager.CHANGE_FLAG);
				outputStream.writeShort((short)drawables.size());
				
				for (Entry<Short, IDrawable> entries : drawables.entrySet()) {
					Short drawableId = entries.getKey();
					writeDrawableToStream(outputStream, drawableId, Short.MAX_VALUE);
				}

				packets = PacketChunker.instance.createPackets(bos.toByteArray());

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
				
				// send the 'change' flag
				outputStream.writeByte(DrawableManager.CHANGE_FLAG);
				
				// write the amount of drawables that have changed
				outputStream.writeShort((short)changes.size());
				
				// write each of the drawables
				for (Entry<Short, Short> change : changes.entrySet()) {
					Short drawableId = change.getKey();
					Short changeMask = change.getValue();
					writeDrawableToStream(outputStream, drawableId, changeMask);
					
				}
				
				packets = PacketChunker.instance.createPackets(bos.toByteArray());

				changes.clear();
			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
		}
		return packets;
	}

	private void writeDrawableToStream(DataOutputStream outputStream, short drawableId, Short changeMask) throws IOException {
		
		// write the mask
		outputStream.writeShort(changeMask);
		
		// write the drawable Id
		outputStream.writeShort(drawableId);
		
		if (ByteUtils.get(changeMask, 0)) { // if its not deleted

			IDrawable drawable = drawables.get(drawableId);

			if (drawable instanceof DrawableText) {
				outputStream.writeByte((byte) 0);
			} else {
				outputStream.writeByte((byte) 1);
			}
			
			// write the rest of the drawable object
			drawable.writeTo(outputStream, changeMask);
		}
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
		return addGradientBox(x, y, width, height, color, alpha, color, alpha, (byte)0);
	}
	
	public ILuaObject addGradientBox(int x, int y, int width, int height, int color, double alpha, int color2, double alpha2, byte gradient) throws InterruptedException {
		ILuaObject obj = null;

		try {
			lock.lock();
			try {
				drawables.put(count, new DrawableBox(this, x, y, width, height, color, alpha, color2, alpha2, gradient));
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
	
	public static TileEntityGlassesBridge getGlassesBridgeFromStack(World worldObj,
			ItemStack stack) {
		if (stack.hasTagCompound()) {

			NBTTagCompound tag = stack.getTagCompound();
			
			if (tag.hasKey("openp")) {
				
				NBTTagCompound openPTag = tag.getCompoundTag("openp");
	
				String guid = openPTag.getString("guid");
				int x = openPTag.getInteger("x");
				int y = openPTag.getInteger("y");
				int z = openPTag.getInteger("z");
				int d = openPTag.getInteger("d");
				if (d == worldObj.provider.dimensionId) {
					if (worldObj.blockExists(x, y, z)) {
						TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
						if (tile instanceof TileEntityGlassesBridge) {
							if (!((TileEntityGlassesBridge)tile).getGuid().equals(guid)) {
								return null;
							}
							return (TileEntityGlassesBridge) tile;
						}
					}
				}
			}
		}
		return null;
	}
	
	public void writeDataToGlasses(ItemStack stack) {
		NBTTagCompound tag = null;
		if (stack.hasTagCompound()) {
			tag = stack.getTagCompound();
		}else {
			tag = new NBTTagCompound();
		}
		
		NBTTagCompound openPTag = null;
		if (tag.hasKey("openp")) {
			openPTag = tag.getCompoundTag("openp");
		}else {
			openPTag = new NBTTagCompound();
		}
		
		openPTag.setString("guid", getGuid());
		openPTag.setInteger("x", xCoord);
		openPTag.setInteger("y", yCoord);
		openPTag.setInteger("z", zCoord);
		openPTag.setInteger("d", worldObj.provider.dimensionId);
		tag.setTag("openp", openPTag);
		stack.setTagCompound(tag);
	}

}

package openperipheral.glasses.block;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import openperipheral.core.interfaces.IAttachable;
import openperipheral.core.interfaces.IDrawable;
import openperipheral.core.interfaces.ISurface;
import openperipheral.core.item.ItemGlasses;
import openperipheral.core.util.MiscUtils;
import openperipheral.core.util.PacketChunker;
import openperipheral.core.util.StringUtils;
import openperipheral.core.util.ThreadLock;
import openperipheral.glasses.client.TerminalManager;
import openperipheral.glasses.drawable.Surface;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaObject;


public class TileEntityGlassesBridge extends TileEntity implements IAttachable,
        ISurface {

    public HashMap<String, Surface> playerSurfaces = new HashMap<String, Surface>();
    public Surface globalSurface = new Surface(this);

    public ArrayList<String> newPlayers = new ArrayList<String>();
    private ArrayList<IComputerAccess> computers = new ArrayList<IComputerAccess>();

    //TODO: Revise all the locks.
    private ThreadLock lock = new ThreadLock();
    
    /**
     * Unique GUID for this terminal
     */
    private String guid = StringUtils.randomString(8);

    public TileEntityGlassesBridge() {
    }

    public void registerPlayer(EntityPlayer player) {
        if (!playerSurfaces.containsKey(player.username)
                && !newPlayers.contains(player.username)) {
            newPlayers.add(player.username);
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote) {
            try {
                /*
                 * If this is a new player, we want to send global list. Maybe
                 * private list too.. I really have no idea what I'm doing :P
                 */
                lock.lock();
                try {
                    if (newPlayers.size() > 0) {
                        // Get this new guy's global packet
                        Packet[] fullPackets = globalSurface.createFullPackets();
                        for (String playerName : newPlayers) {
                            EntityPlayer player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(playerName);
                            // EntityPlayer player =
                            // worldObj.getPlayerEntityByName(playerName);
                            if (player != null) {
                                
                                if(fullPackets != null) {
                                    // Send him the global packets
                                    for (Packet packet : fullPackets) {
                                        ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(packet);
                                    }
                                }
                                
                                // Send him his private stuff if it exists
                                if (playerSurfaces.containsKey(playerName)) {
                                    ISurface surface = playerSurfaces.get(playerName);
                                    
                                    if (surface instanceof Surface) {
                                        Packet[] privatePackets = ((Surface) surface).createFullPackets();
                                        if(privatePackets != null) {
                                            for(Packet pkt : privatePackets) {
                                                ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(pkt);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if (playerSurfaces.keySet().size() > 0) {
                        /* First we get the global change packets */
                        Packet[] globalChangePackets = null;
                        if (globalSurface.hasChanges()) {
                            globalChangePackets = globalSurface.createChangePackets();
                        }
                        /*
                         * Now we start iterating players and sending both
                         * global and private changes
                         */
                        Iterator<String> iter = playerSurfaces.keySet().iterator();
                        while (iter.hasNext()) {
                            String playerName = iter.next();
                            EntityPlayer player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(playerName);
                            if (player == null || !isPlayerValid(player)) {
                                iter.remove();
                                if (player != null) {
                                    sendClearScreenToPlayer(player);
                                }
                            } else {
                                if (globalChangePackets != null && globalChangePackets.length > 0) {
                                    for (Packet packet : globalChangePackets) {
                                        ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(packet);
                                    }
                                }
                                
                                // Well wasn't that lovely, now we need to send the private packet
                                if(playerSurfaces.containsKey(playerName)) {
                                    ISurface surface = playerSurfaces.get(playerName);
                                    if(surface != null && surface instanceof Surface) {
                                        if(((Surface)surface).hasChanges()) {
                                            Packet[] privatePackets = ((Surface)surface).createChangePackets();
                                            if(privatePackets != null) {
                                                for(Packet privatePacket : privatePackets) {
                                                    ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(privatePacket);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    globalSurface.clearChanges();
                    for (String newPlayer : newPlayers) {
                        if (!playerSurfaces.containsKey(newPlayer)) {
                            playerSurfaces.put(newPlayer, new Surface(this, newPlayer));
                        }
                    }
                    newPlayers.clear();
                } catch (Exception e2) {
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
                outputStream.writeByte(TerminalManager.CLEAR_ALL_FLAG);
                byte[] data = bos.toByteArray();
                Packet[] packets = PacketChunker.instance.createPackets(data);
                for (Packet p : packets) {
                    ((EntityPlayerMP) player).playerNetServerHandler
                            .sendPacketToPlayer(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean isPlayerValid(EntityPlayer player) {
        ItemStack glasses = player.inventory.armorItemInSlot(3);
        if (glasses == null)
            return false;
        glasses.getItem();
        if (!MiscUtils.canBeGlasses(glasses))
            return false;
        if (!glasses.hasTagCompound())
            return false;
        NBTTagCompound tag = glasses.getTagCompound();
        if (!tag.hasKey("openp"))
            return false;
        NBTTagCompound openPTag = tag.getCompoundTag("openp");
        if (!openPTag.hasKey("guid"))
            return false;
        return openPTag.getString("guid").equals(guid);
    }

    public String getGuid() {
        return guid;
    }

    public void resetGuid() {
        guid = StringUtils.randomString(8);
    }

    public String[] getUsers() {
        return playerSurfaces.keySet().toArray(
                new String[playerSurfaces.keySet().size()]);
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

    public void onChatCommand(String command, String username) {
        for (IComputerAccess computer : computers) {
            computer.queueEvent("chat_command", new Object[] { command,
                    username, getGuid(), computer.getAttachmentName() });
        }
    }
    
    public void enqueueComputerEvent(String event, String playerName) {
    	for (IComputerAccess computer :computers) {
    		computer.queueEvent(event, new Object[] { playerName });
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

    public static TileEntityGlassesBridge getGlassesBridgeFromStack(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag.hasKey("openp")) {
                NBTTagCompound openPTag = tag.getCompoundTag("openp");
                String guid = openPTag.getString("guid");
                int x = openPTag.getInteger("x");
                int y = openPTag.getInteger("y");
                int z = openPTag.getInteger("z");
                int d = openPTag.getInteger("d");
                
                World worldObj = DimensionManager.getWorld(d);
                
                if (worldObj != null) {
                    if (worldObj.blockExists(x, y, z)) {
                        TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
                        if (tile instanceof TileEntityGlassesBridge) {
                            if (!((TileEntityGlassesBridge) tile).getGuid()
                                    .equals(guid))
                                return null;
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
        } else {
            tag = new NBTTagCompound();
        }
        NBTTagCompound openPTag = null;
        if (tag.hasKey("openp")) {
            openPTag = tag.getCompoundTag("openp");
        } else {
            openPTag = new NBTTagCompound();
        }
        openPTag.setString("guid", getGuid());
        openPTag.setInteger("x", xCoord);
        openPTag.setInteger("y", yCoord);
        openPTag.setInteger("z", zCoord);
        openPTag.setInteger("d", worldObj.provider.dimensionId);
        tag.setTag("openp", openPTag);
        stack.setTagCompound(tag);
        ((ItemGlasses)stack.getItem()).bridge = this;
    }

    @Override
    public Short getKeyForDrawable(IDrawable d) {
        return globalSurface.getKeyForDrawable(d);
    }

    @Override
    public void setDeleted(IDrawable d) {
        globalSurface.setDeleted(d);        
    }

    @Override
    public void markChanged(IDrawable d, int slot) {
        globalSurface.markChanged(d, slot);
    }

    @Override
    public ILuaObject addBox(int x, int y, int width, int height, int color,
            double alpha) throws InterruptedException {
        return globalSurface.addBox(x, y, width, height, color, alpha);
    }

    @Override
    public ILuaObject addGradientBox(int x, int y, int width, int height,
            int color, double alpha, int color2, double alpha2, byte gradient)
            throws InterruptedException {
        return globalSurface.addGradientBox(x, y, width, height, color, alpha, color2, alpha2, gradient);
    }
    
    @Override
    public ILuaObject addLiquid(int x, int y, int width, int height, int id) {
        return globalSurface.addLiquid(x, y, width, height, id);
    }

    @Override
    public ILuaObject getById(int id) {
        return globalSurface.getById(id);
    }

    @Override
    public ILuaObject addText(int x, int y, String text, int color) {
        return globalSurface.addText(x, y, text, color);
    }
    
    @Override
    public ILuaObject addIcon(int x, int y, int id, int meta) {
        return globalSurface.addIcon(x, y, id, meta);
    }

    @Override
    public Short[] getAllIds() {
        return globalSurface.getAllIds();
    }

    @Override
    public void clear() {
        globalSurface.clear();
    }

    public int getStringWidth(String text) {
        return Surface.getStringWidth(text);
    }

    public ILuaObject getUserSurface(String username) {
        if(playerSurfaces.containsKey(username)) {
            return playerSurfaces.get(username);
        }
        return null;
    }

}

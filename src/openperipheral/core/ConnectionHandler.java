package openperipheral.core;

import openperipheral.core.item.ItemGlasses;
import openperipheral.core.util.MiscUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class ConnectionHandler implements IConnectionHandler {
  public static final String PLAYER_EVENT = "registered_player_join";
  
  @Override
  public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
    EntityPlayer realPlayer = netHandler.getPlayer();
    ItemStack helmet = realPlayer.inventory.armorInventory[3];
    if (helmet == null) return;
    if (!MiscUtils.canBeGlasses(helmet)) return;
    ItemGlasses glasses = ((ItemGlasses)helmet.getItem());
    if (glasses.bridge != null) {
      glasses.bridge.enqueueComputerEvent(PLAYER_EVENT, realPlayer.username);
    }
  }

  @Override
  public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
    return null;
  }

  @Override
  public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {}

  @Override
  public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {}

  @Override
  public void connectionClosed(INetworkManager manager) {}

  @Override
  public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {}

}

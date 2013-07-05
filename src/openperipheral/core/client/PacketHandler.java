package openperipheral.core.client;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import openperipheral.core.ConfigSettings;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {

		if (packet.channel.equals(ConfigSettings.NETWORK_CHANNEL)) {
			try {
				ClientProxy.terminalManager.handlePacket(packet);
			} catch (Exception e) {
			}
		}
	}

}

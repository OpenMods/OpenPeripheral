package openperipheral.client;

import net.minecraftforge.common.MinecraftForge;
import openperipheral.common.CommonProxy;

public class ClientProxy extends CommonProxy {

	public static TerminalManager terminalManager = new TerminalManager();

	@Override
	public void registerRenderInformation() {
		MinecraftForge.EVENT_BUS.register(terminalManager);
	}
}

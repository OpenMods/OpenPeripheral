package openperipheral.client;

import net.minecraftforge.common.MinecraftForge;
import openperipheral.common.CommonProxy;
import openperipheral.common.terminal.DrawableManager;

public class ClientProxy extends CommonProxy {

	public DrawableManager drawables = new DrawableManager();

	@Override
	public DrawableManager getDrawableManager() {
		return drawables;
	}
	
	@Override
	public void registerRenderInformation() {
		MinecraftForge.EVENT_BUS.register(getDrawableManager());
	}
}

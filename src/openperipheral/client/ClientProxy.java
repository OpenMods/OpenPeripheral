package openperipheral.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import openperipheral.OpenPeripheral;
import openperipheral.TickHandler;
import openperipheral.common.CommonProxy;
import openperipheral.common.terminal.DrawableManager;
import openperipheral.common.util.FontSizeChecker;

public class ClientProxy extends CommonProxy {

	public DrawableManager drawables = new DrawableManager();

	public static FontSizeChecker fontSizeChecker;
	
	@Override
	public DrawableManager getDrawableManager() {
		return drawables;
	}
	
	@Override
	public void registerRenderInformation() {
		MinecraftForge.EVENT_BUS.register(getDrawableManager());
		fontSizeChecker = new FontSizeChecker(OpenPeripheral.RESOURCE_PATH + "/textures/fonts/main.png");
	}
	
	@Override
	public FontSizeChecker getFontSizeChecker() {
		return fontSizeChecker;
	}
}

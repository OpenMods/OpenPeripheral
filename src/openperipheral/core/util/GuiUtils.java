package openperipheral.core.util;

import java.util.Map;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkModHandler;
import cpw.mods.fml.common.network.NetworkRegistry;

public class GuiUtils {

	public static GuiContainer getGuiContainerForMod(String mod, EntityPlayer player, World world, int x, int y, int z) {
		ModContainer mc = null;
		for (ModContainer container : Loader.instance().getModList()) {
			if (container.getModId().equals(mod)) {
				mc = container;
				break;
			}
		}
		if (mc != null) {
			Object remoteHandler = ReflectionHelper.getProperty("", NetworkRegistry.instance(), "serverGuiHandlers");
			IGuiHandler handler = (IGuiHandler)((Map)remoteHandler).get(mc);
			NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler(mc);
			if (handler != null && nmh != null) { return (GuiContainer)handler.getClientGuiElement(101, player, world, x, y, z); }
		}
		return null;
	}
}

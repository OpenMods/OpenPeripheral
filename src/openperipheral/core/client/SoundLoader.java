package openperipheral.core.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SoundLoader {
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void loadingSounds(SoundLoadEvent event) {
		Minecraft mc = Minecraft.getMinecraft();

		String[] soundFiles = { "ticketmachine.ogg", "robotstepping.ogg", "robotjump.ogg", "robothurt.ogg", "robotdead1.ogg", "robotdead2.ogg", "lazer1.ogg", "robotready.ogg" };

		for (String soundFile : soundFiles) {
			event.manager.addSound("openperipheral:" + soundFile);
		}
	}
}

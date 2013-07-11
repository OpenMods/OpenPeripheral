package openperipheral.core.client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import openperipheral.OpenPeripheral;
import openperipheral.core.ConfigSettings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SoundLoader {
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void loadingSounds(SoundLoadEvent event) {
		Minecraft mc = Minecraft.getMinecraft();

		String[] soundFiles = {
				"ticketmachine.ogg",
				"robotstepping.ogg",
				"robotjump.ogg",
				"robothurt.ogg",
				"robotdead1.ogg",
				"robotdead2.ogg",
				"lazer1.ogg",
				"robotready.ogg"
		};
		
		for (String soundFile : soundFiles) {
			event.manager.soundPoolSounds.addSound("openperipheral:" + soundFile);			
		}
	}
}

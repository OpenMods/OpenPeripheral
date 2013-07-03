package openperipheral.client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import openperipheral.OpenPeripheral;
import openperipheral.common.config.ConfigSettings;
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

		File resourcesDirectory = new File(mc.mcDataDir, "resources/openperipheral/");

		if (!resourcesDirectory.exists()) {
			resourcesDirectory.mkdir();
		}

		for (String fileName : soundFiles) {
			try {
				File soundFile = new File(resourcesDirectory, fileName);
				if (!soundFile.exists()) {

					InputStream streamIn = OpenPeripheral.class.getResourceAsStream(ConfigSettings.RESOURCE_PATH + "/sounds/" + fileName);
					BufferedOutputStream streamOut = new BufferedOutputStream(new FileOutputStream(soundFile));
					byte[] buffer = new byte[1024];
					for (int len = 0; (len = streamIn.read(buffer)) >= 0;) {
						streamOut.write(buffer, 0, len);
					}
					streamIn.close();
					streamOut.close();
				}

				event.manager.soundPoolSounds.addSound("openperipheral/" + fileName, soundFile);
			} catch (Exception e) {
				System.out.println("Couldnt load "+ fileName);
			}
		}
	}
}

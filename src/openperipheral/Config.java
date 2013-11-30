package openperipheral;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import openmods.OpenMods;

public class Config {

	public static String localLuaFolder = "[default]";

	static void readConfig(Configuration configFile) {
		Property prop = configFile.get("general", "luafolder", localLuaFolder, "The location of the lua scripts to mount");
		String folderLoc = prop.getString();

		if (folderLoc.equals(localLuaFolder)) {
			File baseDirectory = OpenMods.proxy.getMinecraftDir();
			File openPeripheralFolder = new File(baseDirectory, "openperipheral/");
			File luaFolder = new File(openPeripheralFolder, "lua/");
			localLuaFolder = luaFolder.getAbsolutePath();
			prop.set(localLuaFolder);
		}

	}
}

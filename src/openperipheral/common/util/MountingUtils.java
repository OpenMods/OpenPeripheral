package openperipheral.common.util;

import java.io.IOException;

import openperipheral.common.config.ConfigSettings;

public class MountingUtils {

	public static void refreshLatestFiles() {

		FileRetriever.downloadFileIfOlderThan(ConfigSettings.EXTERNAL_LUA_LISTING, ConfigSettings.LOCAL_LUA_LISTING, ConfigSettings.CACHE_REFRESH_INTERVAL);

		try {
			FileLineReader.readLineByLine(ConfigSettings.LOCAL_LUA_LISTING, new ILineReadMethod() {
				@Override
				public void Read(String line) {
					FileRetriever.downloadFileIfOlderThan(String.format("%s%s", ConfigSettings.EXTERNAL_LUA_FOLDER, line),
							String.format("%s/%s", ConfigSettings.LOCAL_LUA_FOLDER, line), ConfigSettings.CACHE_REFRESH_INTERVAL);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

package openperipheral.util;

import java.io.IOException;

import openmods.utils.FileLineReader;
import openmods.utils.ILineReadMethod;
import openperipheral.ConfigSettings;

public class MountingUtils {

	public static void refreshLatestFiles() {

		FileRetriever.downloadFileIfOlderThan(
			ConfigSettings.EXTERNAL_LUA_LISTING,
			ConfigSettings.LOCAL_LUA_LISTING,
			ConfigSettings.CACHE_REFRESH_INTERVAL
		);

		try {
			FileLineReader.readLineByLine(ConfigSettings.LOCAL_LUA_LISTING, new ILineReadMethod() {
				@Override
				public void read(String line) {
					FileRetriever.downloadFileIfOlderThan(
						String.format(
								"%s%s",
								ConfigSettings.EXTERNAL_LUA_FOLDER,
								line
						),
						String.format(
								"%s/%s",
								ConfigSettings.LOCAL_LUA_FOLDER,
								line
						),
						ConfigSettings.CACHE_REFRESH_INTERVAL
					);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

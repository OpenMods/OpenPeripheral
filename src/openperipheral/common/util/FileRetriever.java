package openperipheral.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import openperipheral.common.config.ConfigSettings;

public class FileRetriever {

	public static void downloadFileIfOlderThan(String external, String local, int days) {

		File file = new File(local);

		file.getParentFile().mkdirs();

		if (!file.exists() || (file.lastModified() < System.currentTimeMillis() - (ConfigSettings.CACHE_REFRESH_INTERVAL * 24 * 60 * 60 * 1000))) {

			BufferedInputStream in = null;
			FileOutputStream fout = null;
			try {
				in = new BufferedInputStream(new URL(external).openStream());
				fout = new FileOutputStream(local);

				byte data[] = new byte[1024];
				int count;
				while ((count = in.read(data, 0, 1024)) != -1) {
					fout.write(data, 0, count);
				}
			} catch (Exception e) {
			}
			try {
				if (in != null)
					in.close();
				if (fout != null)
					fout.close();
			} catch (IOException e) {
			}
		}

	}

}

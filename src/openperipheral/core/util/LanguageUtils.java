package openperipheral.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import openperipheral.core.CommonProxy;
import openperipheral.core.ConfigSettings;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class LanguageUtils {
	public static void setupLanguages() {

		try {
			InputStream input = CommonProxy.class.getResourceAsStream(String.format("%s/languages.txt", ConfigSettings.LANGUAGE_PATH));

			if (input == null) { return; }

			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

			FileLineReader.readLineByLine(reader, new ILineReadMethod() {
				@Override
				public void Read(String line) {
					URL url = CommonProxy.class.getResource(String.format("%s/%s.lang", ConfigSettings.LANGUAGE_PATH, line));
					if (url == null) { return; }
					LanguageRegistry.instance().loadLocalization(url, line, false);
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

	}
}
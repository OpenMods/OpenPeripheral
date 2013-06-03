package openperipheral.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import openperipheral.OpenPeripheral;
import openperipheral.common.CommonProxy;
import cpw.mods.fml.common.registry.LanguageRegistry;


public class LanguageUtils {
	public static void setupLanguages() {

		ArrayList arrayList = new ArrayList();

		try {
			InputStream input = CommonProxy.class.getResourceAsStream(String
					.format("%s/languages.txt", OpenPeripheral.LANGUAGE_PATH));

			if (input == null) {
				return;
			}

			BufferedReader var2 = new BufferedReader(new InputStreamReader(
					input, "UTF-8"));

			for (String var3 = var2.readLine(); var3 != null; var3 = var2
					.readLine()) {
				arrayList.add(var3);
			}
		} catch (IOException var5) {
			var5.printStackTrace();
			return;
		}

		Iterator iterator = arrayList.iterator();

		while (iterator.hasNext()) {
			String langString = (String) iterator.next();
			URL url = CommonProxy.class.getResource(String.format("%s/%s.lang",
					OpenPeripheral.LANGUAGE_PATH, langString));
			if (url == null) {
				continue;
			}
			LanguageRegistry.instance()
					.loadLocalization(url, langString, false);
		}
	}
}
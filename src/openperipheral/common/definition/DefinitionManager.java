package openperipheral.common.definition;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import openperipheral.common.config.ConfigSettings;
import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

public class DefinitionManager {

	public static HashMap<Class, DefinitionClass> classList = new HashMap<Class, DefinitionClass>();

	public static void load() {
		JsonRootNode rootNode = loadJSON();
		if (rootNode != null) {
			for (JsonNode modNode : rootNode.getElements()) {
				DefinitionMod definition = new DefinitionMod(modNode);
				classList.putAll(definition.getValidClasses());
			}
		}
	}

	public static ArrayList<DefinitionMethod> getMethodsForClass(Class klass) {
		ArrayList<DefinitionMethod> methods = new ArrayList<DefinitionMethod>();
		for (Entry<Class, DefinitionClass> entry : classList.entrySet()) {
			if (entry.getKey().isAssignableFrom(klass)) {
				methods.addAll(entry.getValue().getMethods());
			}
		}
		return methods;
	}

	private static JsonRootNode loadJSON() {

		File file = new File(ConfigSettings.CACHE_PATH);
		if (!file.exists() || (file.lastModified() < System.currentTimeMillis() - (ConfigSettings.CACHE_REFRESH_INTERVAL * 24 * 60 * 60 * 1000))) {
			fetchNewData();
		}

		try {
			System.out.println("Parsing openperipheral json");
			BufferedReader br = new BufferedReader(new FileReader(ConfigSettings.CACHE_PATH));
			JdomParser parser = new JdomParser();
			JsonRootNode root = parser.parse(br);
			return root;
		} catch (Exception e) {
			System.out.println("Unable to parse openperipherals");
		}

		return null;
	}

	private static void fetchNewData() {
		System.out.println("Fetching new openperipherals data from " + ConfigSettings.DATA_URL);
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(new URL(ConfigSettings.DATA_URL).openStream());
			fout = new FileOutputStream(ConfigSettings.CACHE_PATH);

			byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) {
				fout.write(data, 0, count);
			}
		} catch (Exception e) {
			System.out.println("Error fetching openperipheral data");
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

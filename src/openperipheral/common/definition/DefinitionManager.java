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
import openperipheral.common.util.FileRetriever;
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

		FileRetriever.downloadFileIfOlderThan(
			ConfigSettings.DATA_URL,
			ConfigSettings.CACHE_PATH,
			ConfigSettings.CACHE_REFRESH_INTERVAL
		);
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(ConfigSettings.CACHE_PATH));
			JdomParser parser = new JdomParser();
			JsonRootNode root = parser.parse(br);
			return root;
		} catch (Exception e) {
			System.out.println("Unable to parse openperipherals method data");
		}

		return null;
	}

}

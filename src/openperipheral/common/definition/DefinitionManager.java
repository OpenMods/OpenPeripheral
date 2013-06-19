package openperipheral.common.definition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.api.IMethodDefinition;
import openperipheral.common.config.ConfigSettings;
import openperipheral.common.util.FileRetriever;
import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

public class DefinitionManager {

	public static HashMap<Class, ArrayList<IClassDefinition>> classList = new HashMap<Class, ArrayList<IClassDefinition>>();
	
	public static void load() {
		JsonRootNode rootNode = loadJSON();
		if (rootNode != null) {
			for (JsonNode modNode : rootNode.getElements()) {
				DefinitionJsonMod definition = new DefinitionJsonMod(modNode);
				for (Entry<? extends Class, ? extends IClassDefinition> defs : definition.getValidClasses().entrySet()) {
					addClass(defs.getKey(), defs.getValue());
				}
			}
		}
	}
	
	public static void addClassDefinition(IClassDefinition classDefinition) {
		if (classDefinition.getJavaClass() != null) {
			addClass(classDefinition.getJavaClass(), classDefinition);
		}
	}
	
	public static void addClass(Class klazz, IClassDefinition classDefinition) {
		if (classList.containsKey(klazz)) {
			classList.get(klazz).add(classDefinition);
		}else {
			ArrayList<IClassDefinition> newlist = new ArrayList<IClassDefinition>();
			newlist.add(classDefinition);
			classList.put(klazz, newlist);
		}
	}
	
	public static ArrayList<IMethodDefinition> getMethodsForTile(TileEntity tile) {
		ArrayList<IMethodDefinition> methods = new ArrayList<IMethodDefinition>();
		for (Entry<Class, ArrayList<IClassDefinition>> entry : classList.entrySet()) {
			if (entry.getKey().isAssignableFrom(tile.getClass())) {
				for (IClassDefinition def : entry.getValue()) {
					methods.addAll(def.getMethods(tile));
				}
			}
		}
		return methods;
	}

	private static JsonRootNode loadJSON() {

		FileRetriever.downloadFileIfOlderThan(ConfigSettings.DATA_URL, ConfigSettings.CACHE_PATH, ConfigSettings.FRESH_INSTALL ? 0
				: ConfigSettings.CACHE_REFRESH_INTERVAL);

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

package openperipheral.core.restriction;

import java.util.HashMap;
import java.util.Map.Entry;

import openperipheral.api.IRestriction;
import openperipheral.core.interfaces.IRestrictionHandler;
import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;

public class RestrictionFactory {

	public static HashMap<String, IRestrictionHandler> restrictionHandlers = new HashMap<String, IRestrictionHandler>();

	public static void registerRestrictionHandler(String key, IRestrictionHandler restriction) {
		restrictionHandlers.put(key, restriction);
	}

	public static IRestriction createFromJson(Entry<JsonStringNode, JsonNode> json) {

		String restrictionKey = json.getKey().getText();

		IRestrictionHandler handler = restrictionHandlers.get(restrictionKey);

		if (handler != null) {
			return handler.createFromJson(json.getValue());
		}

		return null;
	}
}

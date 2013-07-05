package openperipheral.core.restriction;

import java.util.HashMap;

import openperipheral.api.IRestriction;
import openperipheral.core.interfaces.IRestrictionHandler;
import argo.jdom.JsonField;

public class RestrictionFactory {

	public static HashMap<String, IRestrictionHandler> restrictionHandlers = new HashMap<String, IRestrictionHandler>();

	public static void registerRestrictionHandler(String key, IRestrictionHandler restriction) {
		restrictionHandlers.put(key, restriction);
	}

	public static IRestriction createFromJson(JsonField json) {

		String restrictionKey = json.getName().getText();

		IRestrictionHandler handler = restrictionHandlers.get(restrictionKey);

		if (handler != null) {
			return handler.createFromJson(json.getValue());
		}

		return null;
	}
}

package openperipheral;

import java.util.HashMap;

import argo.jdom.JsonField;
import argo.jdom.JsonNode;

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

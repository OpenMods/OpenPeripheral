package openperipheral.api;

import argo.jdom.JsonNode;

public interface IRestrictionHandler {
	public IRestriction createFromJson(JsonNode json);
}

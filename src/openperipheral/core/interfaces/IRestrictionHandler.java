package openperipheral.core.interfaces;

import openperipheral.api.IRestriction;
import argo.jdom.JsonNode;

public interface IRestrictionHandler {
	public IRestriction createFromJson(JsonNode json);
}

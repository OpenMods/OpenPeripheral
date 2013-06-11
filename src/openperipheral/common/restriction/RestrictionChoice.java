package openperipheral.common.restriction;

import java.util.ArrayList;

import openperipheral.api.IRestriction;
import argo.jdom.JsonNode;

public class RestrictionChoice implements IRestriction {

	private ArrayList<String> choices = new ArrayList<String>();

	public RestrictionChoice(JsonNode json) {
		for (JsonNode choice : json.getElements()) {
			choices.add("" + choice.getText());
		}
	}

	@Override
	public boolean isValid(Object value) {
		return choices.contains(value);
	}

	@Override
	public String getErrorMessage(int paramOffset) {
		return String.format("Argument number %s must be one of the following: %s", paramOffset, choices.toString());
	}

}

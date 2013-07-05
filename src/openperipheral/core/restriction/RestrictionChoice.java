package openperipheral.core.restriction;

import java.util.ArrayList;
import java.util.Arrays;

import openperipheral.api.IRestriction;
import argo.jdom.JsonNode;

public class RestrictionChoice implements IRestriction {

	private ArrayList<String> choices = new ArrayList<String>();

	public RestrictionChoice(String ... schoices) {
		choices.addAll(Arrays.asList(schoices));
	}
	
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

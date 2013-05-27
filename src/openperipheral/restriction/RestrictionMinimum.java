package openperipheral.restriction;

import argo.jdom.JsonNode;
import openperipheral.IRestriction;

public class RestrictionMinimum implements IRestriction {

	private int minimum;
	
	public RestrictionMinimum(JsonNode json) {
		minimum = Integer.parseInt(json.getText());
	}
	
	@Override
	public boolean isValid(Object value) {
		return (Integer)value > minimum;
	}

	@Override
	public String getErrorMessage(int paramOffset) {
		return String.format("Arguments %s must be higher than %s", paramOffset, minimum);
	}
}

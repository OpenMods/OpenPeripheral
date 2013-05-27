package openperipheral.restriction;

import argo.jdom.JsonNode;
import openperipheral.IRestriction;

public class RestrictionMaximum implements IRestriction {

	private int maximum;
	
	public RestrictionMaximum(JsonNode json) {
		maximum = Integer.parseInt(json.getText());
	}
	
	@Override
	public boolean isValid(Object value) {
		return (Integer)value < maximum;
	}

	@Override
	public String getErrorMessage(int paramOffset) {
		return String.format("Arguments %s must be lower than %s", paramOffset, maximum);
	}

}

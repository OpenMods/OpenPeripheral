package openperipheral.common.restriction;

import openperipheral.api.IRestriction;
import argo.jdom.JsonNode;

public class RestrictionMaximum implements IRestriction {

	private int maximum;

	public RestrictionMaximum(JsonNode json) {
		maximum = Integer.parseInt(json.getText());
	}
	
	public RestrictionMaximum(int max) {
		maximum = max;
	}

	@Override
	public boolean isValid(Object value) {
		if (value instanceof Byte) {
			return (Byte) value <= maximum;
		} else if (value instanceof Double) {
			return (Double) value <= maximum;
		}
		return (Integer) value <= maximum;
	}

	@Override
	public String getErrorMessage(int paramOffset) {
		return String.format("Argument %s must be no bigger than %s", paramOffset, maximum);
	}

}

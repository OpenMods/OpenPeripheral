package openperipheral.common.restriction;

import openperipheral.api.IRestriction;

public class RestrictionLength implements IRestriction {

	private int requiredLength = 0;
	
	public RestrictionLength(int requiredLength) {
		this.requiredLength = requiredLength;
	}
	
	@Override
	public boolean isValid(Object value) {
		return value instanceof String && ((String)value).length() == requiredLength;
	}

	@Override
	public String getErrorMessage(int paramOffset) {
		return String.format("Arguments %s must exactly %s characters long", paramOffset, requiredLength);
	}

}

package openperipheral.adapter;

import java.util.Locale;

public enum DefaultAttributeProperty implements IAttributeProperty {
	NULLABLE, OPTIONAL, VARIADIC;

	@Override
	public String id() {
		return name().toLowerCase(Locale.ROOT);
	}

}

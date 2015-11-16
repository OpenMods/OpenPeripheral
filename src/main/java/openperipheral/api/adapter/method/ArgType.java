package openperipheral.api.adapter.method;

import java.util.Locale;

public enum ArgType {

	TABLE,
	NUMBER,
	STRING,
	VOID,
	BOOLEAN,
	OBJECT,
	/**
	 * <p>
	 * This value usually causes OpenPeripheral to try to automatically deduct type of parameter. Few basic rules exist (for primitive types, arrays, collections), but they can be extended via {@link ITypeQualifier}.
	 * </p>
	 *
	 * <p>
	 * <strong>Note:</strong> if deduction fails, OpenPeripheral will skip wrapper generation for user type.
	 * </p>
	 */
	AUTO {
		@Override
		public String getName() {
			return "<invalid>";
		}
	};

	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}

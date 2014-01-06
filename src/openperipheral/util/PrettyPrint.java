package openperipheral.util;

import openmods.Log;

import com.google.common.base.Strings;

public class PrettyPrint {
	public static String getMessageForThrowable(Throwable e) {
		Throwable cause = e.getCause();

		String firstMessage = e.getMessage();
		String secondMessage = (cause != null)? cause.getMessage() : null;

		final boolean firstEmpty = Strings.isNullOrEmpty(firstMessage);
		final boolean secondEmpty = Strings.isNullOrEmpty(secondMessage);

		if (firstEmpty && secondEmpty) {
			Log.warn(e, "Exception without message");
			return String.format("Unknown expection %s. Please contact devs on esper.net IRC #OpenMods", e.getClass());
		} else if (!firstEmpty && !secondEmpty) return String.format("%s, caused by %s", firstMessage, secondMessage);

		return firstEmpty? secondMessage : firstMessage;
	}
}

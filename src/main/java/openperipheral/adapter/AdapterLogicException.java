package openperipheral.adapter;

import com.google.common.base.Strings;

public class AdapterLogicException extends RuntimeException {
	private static final long serialVersionUID = 162027349454188794L;

	public AdapterLogicException(Throwable cause) {
		super(cause);
	}

	public static String getMessageForThrowable(Throwable e) {
		Throwable cause = e.getCause();

		String firstMessage = e.getMessage();
		String secondMessage = (cause != null)? cause.getMessage() : null;

		final boolean firstEmpty = Strings.isNullOrEmpty(firstMessage);
		final boolean secondEmpty = Strings.isNullOrEmpty(secondMessage);

		if (firstEmpty && secondEmpty) {
			return String.format("Caught exception %s without any info", e.getClass());
		} else if (!firstEmpty && !secondEmpty) return String.format("%s, caused by %s", firstMessage, secondMessage);

		return firstEmpty? secondMessage : firstMessage;
	}

	@Override
	public String getMessage() {
		Throwable cause = getCause();
		return cause != null? getMessageForThrowable(cause) : "internal error";
	}
}

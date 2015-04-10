package openperipheral.api.adapter;

import openperipheral.api.architecture.cc.IComputerCraftObjectsFactory;
import openperipheral.api.architecture.oc.IOpenComputersObjectsFactory;

/**
 * This exception is throwed from wrapped object factories.
 * Generation usually fails due to invalid contents of wrapped classes, like client only methods.
 *
 * @see IOpenComputersObjectsFactory
 * @see IComputerCraftObjectsFactory
 */
public class GenerationFailedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public GenerationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public GenerationFailedException(String message) {
		super(message);
	}

	public GenerationFailedException(Throwable cause) {
		super(cause);
	}

}

package openperipheral.adapter;

import openperipheral.TypeConvertersProvider;
import openperipheral.api.ITypeConvertersRegistry;

public class DefaultEnvArgs {
	public static final String ARG_CONTEXT = "context";
	public static final String ARG_COMPUTER = "computer";
	public static final String ARG_TARGET = "target";
	public static final String ARG_CONVERTER = "converter";

	public static IMethodCall addCommonArgs(IMethodCall call, String architecture) {
		ITypeConvertersRegistry converter = TypeConvertersProvider.INSTANCE.getConverter(architecture);
		return call.setOptionalArg(ARG_CONVERTER, converter);
	}
}

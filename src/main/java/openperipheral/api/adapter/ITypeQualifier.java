package openperipheral.api.adapter;

import java.lang.reflect.Type;

import openperipheral.api.IApiInterface;
import openperipheral.api.adapter.method.ArgType;

/**
 * Simple utility for converting Java types into script types.
 *
 * @see ArgType#AUTO
 */
public interface ITypeQualifier extends IApiInterface {

	public interface IClassQualifier {
		public IScriptType qualify(Class<?> cls);
	}

	public interface IGenericQualifier {
		public IScriptType qualify(Type type);
	}

	public void registerQualifier(IClassQualifier qualifier);

	public void registerQualifier(IGenericQualifier qualifier);

	public void registerType(Class<?> cls, IScriptType type);

	public IScriptType qualifyType(Type type);
}

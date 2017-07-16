package openperipheral.api.adapter;

import java.lang.reflect.Type;
import openperipheral.api.IApiInterface;

/**
 * Simple utility for converting Java types into script types.
 */
public interface ITypeClassifier extends IApiInterface {

	public interface IClassClassifier {
		public IScriptType classify(ITypeClassifier classifier, Class<?> cls);
	}

	public interface IGenericClassifier {
		public IScriptType classify(ITypeClassifier classifier, Type type);
	}

	public void registerClassifier(IClassClassifier qualifier);

	public void registerClassifier(IGenericClassifier qualifier);

	public void registerType(Class<?> cls, IScriptType type);

	public IScriptType classifyType(Type type);
}

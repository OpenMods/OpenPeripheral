package openperipheral.adapter.method;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import java.util.Iterator;
import openperipheral.adapter.ArgumentDescriptionBase;
import openperipheral.adapter.types.classifier.TypeClassifier;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.converter.IConverter;

public class Argument extends ArgumentDescriptionBase {

	private final IScriptType scriptType;
	protected final TypeToken<?> javaType;
	public final int javaArgIndex;

	public Argument(String name, String description, TypeToken<?> argJavaType, int javaArgIndex) {
		super(name, description);
		this.javaArgIndex = javaArgIndex;
		this.javaType = getValueType(argJavaType);
		this.scriptType = TypeClassifier.INSTANCE.classifyType(this.javaType.getType());
	}

	protected TypeToken<?> getValueType(TypeToken<?> javaArgClass) {
		return javaArgClass;
	}

	public Object convert(IConverter converter, Iterator<Object> args) {
		Preconditions.checkArgument(args.hasNext(), "Not enough arguments, first missing: %s", name);
		Object arg = args.next();
		Preconditions.checkArgument(arg != null, "Argument %s cannot be null", name);
		return convertSingleArg(converter, arg);
	}

	protected final Object convertSingleArg(IConverter converter, Object o) {
		try {
			return converter.toJava(o, javaType.getType());
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("Failed to convert arg '%s', cause: '%s'", name, e.getMessage()));
		}
	}

	@Override
	public IScriptType type() {
		return scriptType;
	}

	@Override
	public String toString() {
		return name + "(" + javaArgIndex + "):" + javaType;
	}
}
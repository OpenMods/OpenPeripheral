package openperipheral.adapter.property;

import java.lang.reflect.Field;

import openperipheral.api.converter.IConverter;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;

public class GetterExecutor implements IPropertyExecutor {

	private final Field field;

	private final ICallbackProvider provider;

	public GetterExecutor(Field field, ICallbackProvider provider) {
		this.field = field;
		this.provider = provider;
	}

	@Override
	public Object[] call(IConverter converter, Object target, Object... args) {
		Preconditions.checkArgument(args.length == 0, "Getter has no arguments");
		Object result = provider.getCallback(target).getField(field);
		return ArrayUtils.toArray(converter.fromJava(result));
	}
}
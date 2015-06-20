package openperipheral.adapter.property;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import openperipheral.api.converter.IConverter;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;

public class MergedGetterExecutor implements IPropertyExecutor {

	private final Field field;

	private final IFieldManipulator manipulator;

	private final IIndexedFieldManipulator indexedManipulator;

	private final Type keyType;

	public MergedGetterExecutor(Field field, IFieldManipulator manipulator, IIndexedFieldManipulator indexedManipulator, Type keyType) {
		this.field = field;
		this.manipulator = manipulator;
		this.indexedManipulator = indexedManipulator;
		this.keyType = keyType;
	}

	@Override
	public Object[] call(IConverter converter, Object target, Object... args) {

		final Object result;
		if (args.length == 0) {
			result = manipulator.getField(target, field);
		} else if (args.length == 1) {
			final Object key = converter.toJava(args[0], keyType);
			Preconditions.checkArgument(key != null, "Invalid index");
			result = indexedManipulator.getField(target, field, key);
		} else {
			throw new IllegalArgumentException("Getter can have at most one parameter (index)");
		}

		final Object converted = converter.fromJava(result);
		return ArrayUtils.toArray(converted);
	}

}

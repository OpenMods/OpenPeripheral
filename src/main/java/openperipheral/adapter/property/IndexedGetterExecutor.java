package openperipheral.adapter.property;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import openperipheral.api.converter.IConverter;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;

public class IndexedGetterExecutor implements IPropertyExecutor {

	private final Field field;

	private final IIndexedFieldManipulator manipulator;

	private final Type indexType;

	public IndexedGetterExecutor(Field field, IIndexedFieldManipulator manipulator, Type indexType) {
		this.field = field;
		this.manipulator = manipulator;
		this.indexType = indexType;
	}

	@Override
	public Object[] call(IConverter converter, Object target, Object... args) {
		Preconditions.checkArgument(args.length == 1, "Getter should have exactly one argument (index)");
		final Object index = converter.toJava(args[0], indexType);
		Preconditions.checkArgument(index != null, "Invalid index");
		final Object result = manipulator.getField(target, field, index);
		final Object converted = converter.fromJava(result);
		return ArrayUtils.toArray(converted);
	}

}

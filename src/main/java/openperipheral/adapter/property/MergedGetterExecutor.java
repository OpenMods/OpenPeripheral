package openperipheral.adapter.property;

import java.lang.reflect.Field;

import openperipheral.api.converter.IConverter;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;

public class MergedGetterExecutor implements IPropertyExecutor {

	private final Field field;

	private final IFieldManipulator manipulator;

	private final IIndexedFieldManipulator indexedManipulator;

	private final IndexedTypeInfo indexedTypeInfo;

	public MergedGetterExecutor(Field field, IFieldManipulator manipulator, IIndexedFieldManipulator indexedManipulator, IndexedTypeInfo indexedTypeInfo) {
		this.field = field;
		this.manipulator = manipulator;
		this.indexedManipulator = indexedManipulator;
		this.indexedTypeInfo = indexedTypeInfo;
	}

	@Override
	public Object[] call(IConverter converter, Object owner, Object... args) {
		final Object result;
		if (args.length == 0) {
			final Object target = PropertyUtils.getContents(owner, field);
			result = manipulator.getField(owner, target, field);
		} else if (args.length == 1) {
			final Object key = converter.toJava(args[0], indexedTypeInfo.keyType);
			Preconditions.checkArgument(key != null, "Invalid index");
			final Object target = PropertyUtils.getContents(owner, field);
			result = indexedManipulator.getField(owner, target, field, key);
		} else {
			throw new IllegalArgumentException("Getter can have at most one parameter (index)");
		}

		final Object converted = converter.fromJava(result);
		return ArrayUtils.toArray(converted);
	}

}

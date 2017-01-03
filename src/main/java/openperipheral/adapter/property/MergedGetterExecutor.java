package openperipheral.adapter.property;

import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import openperipheral.api.converter.IConverter;
import org.apache.commons.lang3.ArrayUtils;

public class MergedGetterExecutor implements IPropertyExecutor {

	private final Field field;

	private final IFieldManipulator manipulator;

	private final ISinglePropertyAccessHandler singleAccessHandler;

	private final IIndexedFieldManipulator indexedManipulator;

	private final IndexedTypeInfo indexedTypeInfo;

	private final IIndexedPropertyAccessHandler indexedAccessHandler;

	public MergedGetterExecutor(Field field, IFieldManipulator manipulator, ISinglePropertyAccessHandler singleAccessHandler, IIndexedFieldManipulator indexedManipulator, IndexedTypeInfo indexedTypeInfo, IIndexedPropertyAccessHandler indexedAccessHandler) {
		this.field = field;
		this.manipulator = manipulator;
		this.singleAccessHandler = singleAccessHandler;
		this.indexedManipulator = indexedManipulator;
		this.indexedTypeInfo = indexedTypeInfo;
		this.indexedAccessHandler = indexedAccessHandler;
	}

	@Override
	public Object[] call(IConverter converter, Object owner, Object... args) {
		final Object result;
		if (args.length == 0) {
			final Object target = PropertyUtils.getContents(owner, field);
			singleAccessHandler.onGet(owner, target, field);
			result = manipulator.getField(owner, target, field);
		} else if (args.length == 1) {
			final Object key = converter.toJava(args[0], indexedTypeInfo.keyType);
			Preconditions.checkArgument(key != null, "Invalid index");
			final Object target = PropertyUtils.getContents(owner, field);
			indexedAccessHandler.onGet(owner, target, field, key);
			result = indexedManipulator.getField(owner, target, field, key);
		} else {
			throw new IllegalArgumentException("Getter can have at most one parameter (index)");
		}

		final Object converted = converter.fromJava(result);
		return ArrayUtils.toArray(converted);
	}

}

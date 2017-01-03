package openperipheral.adapter.property;

import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import openperipheral.api.converter.IConverter;
import org.apache.commons.lang3.ArrayUtils;

public class GetterExecutor implements IPropertyExecutor {

	private final Field field;

	private final IFieldManipulator manipulator;

	private final ISinglePropertyAccessHandler accessHandler;

	public GetterExecutor(Field field, IFieldManipulator manipulator, ISinglePropertyAccessHandler accessHandler) {
		this.field = field;
		this.manipulator = manipulator;
		this.accessHandler = accessHandler;
	}

	@Override
	public Object[] call(IConverter converter, Object owner, Object... args) {
		Preconditions.checkArgument(args.length == 0, "Getter has no arguments");

		final Object target = PropertyUtils.getContents(owner, field);
		accessHandler.onGet(owner, target, field);
		final Object result = manipulator.getField(owner, target, field);
		final Object converted = converter.fromJava(result);
		return ArrayUtils.toArray(converted);
	}
}
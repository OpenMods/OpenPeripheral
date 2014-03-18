package openperipheral.integration.multiparts;

import java.lang.reflect.Method;
import java.util.List;

import openmods.utils.ReflectionHelper;
import openperipheral.api.*;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

public class AdapterMultipart implements IPeripheralAdapter {

	private static final Class<?> TARGET_CLASS = ReflectionHelper.getClass("codechicken.multipart.TileMultipart");

	private static final Class<?> PART_CLASS = ReflectionHelper.getClass("codechicken.multipart.TMultiPart");

	@Override
	public Class<?> getTargetClass() {
		return TARGET_CLASS;
	}

	@Prefixed("target")
	@LuaCallable(returnTypes = LuaType.TABLE, description = "List types of parts in multipart block")
	public List<String> getParts(Object target) {
		try {
			Method partListMethod = TARGET_CLASS.getMethod("jPartList");
			Method getTypeMethod = PART_CLASS.getMethod("getType");

			@SuppressWarnings("rawtypes")
			List partList = (List)partListMethod.invoke(target);

			List<String> result = Lists.newArrayList();

			for (Object part : partList) {
				String type = (String)getTypeMethod.invoke(part);
				result.add(type);
			}

			return result;
		} catch (Throwable t) {
			throw Throwables.propagate(t);

		}
	}

}

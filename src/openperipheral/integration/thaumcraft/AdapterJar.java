package openperipheral.integration.thaumcraft;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import openmods.utils.ReflectionHelper;
import openperipheral.api.*;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

import com.google.common.base.Preconditions;

@Prefixed("target")
public class AdapterJar implements IPeripheralAdapter {

	private static final Class<?> TILE_JAR_FILLABLE_CLASS = ReflectionHelper.getClass("thaumcraft.common.tiles.TileJarFillable");

	@Override
	public Class<?> getTargetClass() {
		return TILE_JAR_FILLABLE_CLASS;
	}

	protected Aspect getFilterAspect(Object target) throws Exception {
		Field f = ReflectionHelper.getField(TILE_JAR_FILLABLE_CLASS, "aspectFilter");
		Object o = f.get(target);

		if (o == null) return null;
		Preconditions.checkState(o instanceof Aspect, "Invalid stuff in jar");
		return (Aspect)o;
	}

	@LuaMethod(returnType = LuaType.TABLE, description = "Get the aspect filtered by this block block")
	public String getAspectFilter(Object target) throws Exception {
		Aspect aspect = getFilterAspect(target);
		return aspect != null? aspect.getName() : null;
	}

	// special casing jar, for TT compatibility
	@LuaMethod(returnType = LuaType.TABLE, description = "Get the Aspects stored in the block")
	public List<Map<String, Object>> getAspects(IAspectContainer container) throws Exception {
		List<Map<String, Object>> result = AdapterAspectContainer.aspectsToMap(container);
		if (result.isEmpty()) {
			Aspect filter = getFilterAspect(container);
			if (filter != null) AdapterAspectContainer.appendAspectEntry(result, filter, 0);
		}

		return result;
	}

}

package openperipheral.integration.thaumcraft;

import java.util.List;
import java.util.Map;

import openmods.utils.ReflectionHelper;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaCallable;
import openperipheral.api.LuaType;
import openperipheral.util.FieldAccessHelpers;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

public class AdapterJar implements IPeripheralAdapter {

	private static final Class<?> TILE_JAR_FILLABLE_CLASS = ReflectionHelper.getClass("thaumcraft.common.tiles.TileJarFillable");

	@Override
	public Class<?> getTargetClass() {
		return TILE_JAR_FILLABLE_CLASS;
	}

	protected Aspect getFilterAspect(Object target) {
		return FieldAccessHelpers.getField(TILE_JAR_FILLABLE_CLASS, target, "aspectFilter", null);
	}

	@LuaCallable(returnTypes = LuaType.TABLE, description = "Get the aspect filtered by this block block")
	public String getAspectFilter(Object target) {
		Aspect aspect = getFilterAspect(target);
		return aspect != null? aspect.getName() : "";
	}

	// special casing jar, for TT compatibility
	@LuaCallable(returnTypes = LuaType.TABLE, description = "Get the Aspects stored in the block")
	public List<Map<String, Object>> getAspects(IAspectContainer container) {
		List<Map<String, Object>> result = AdapterAspectContainer.aspectsToMap(container);
		if (result.isEmpty()) {
			Aspect filter = getFilterAspect(container);
			if (filter != null) AdapterAspectContainer.appendAspectEntry(result, filter, 0);
		}

		return result;
	}

}

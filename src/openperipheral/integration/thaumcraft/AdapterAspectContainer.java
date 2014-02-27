package openperipheral.integration.thaumcraft;

import java.util.List;
import java.util.Map;

import openperipheral.api.*;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@OnTick
@Prefixed("target")
public class AdapterAspectContainer implements IPeripheralAdapter {
	@Override
	public Class<?> getTargetClass() {
		return IAspectContainer.class;
	}

	public static List<Map<String, Object>> aspectsToMap(IAspectContainer container) {
		List<Map<String, Object>> aspects = Lists.newArrayList();
		AspectList aspectList = container.getAspects();
		if (aspectList == null) return aspects;

		for (Aspect aspect : aspectList.getAspects()) {
			if (aspect == null) continue;
			appendAspectEntry(aspects, aspect, aspectList.getAmount(aspect));
		}
		return aspects;
	}

	public static void appendAspectEntry(List<Map<String, Object>> result, Aspect aspect, int quantity) {
		Map<String, Object> aspectDetails = Maps.newHashMap();
		aspectDetails.put("name", aspect.getName());
		aspectDetails.put("quantity", quantity);
		result.add(aspectDetails);
	}

	@LuaCallable(returnTypes = LuaType.TABLE, description = "Get the Aspects stored in the block")
	public List<Map<String, Object>> getAspects(IAspectContainer container) {
		return aspectsToMap(container);
	}

	@LuaCallable(returnTypes = LuaType.TABLE, description = "Get the map of aspects stored in the block (summed, if there are multiple entries)")
	public Map<String, Integer> getAspectsSum(IAspectContainer container) {
		AspectList aspectList = container.getAspects();
		if (aspectList == null) return null;
		Map<String, Integer> result = Maps.newHashMap();
		for (Aspect aspect : aspectList.getAspects()) {
			if (aspect == null) continue;
			String name = aspect.getName();
			int amount = Objects.firstNonNull(result.get(name), 0);
			result.put(name, amount + aspectList.getAmount(aspect));
		}
		return result;
	}

	@LuaCallable(returnTypes = LuaType.NUMBER, description = "Get amount of specific aspect stored in this block")
	public int getAspectCount(IAspectContainer container,
			@Arg(name = "aspect", type = LuaType.STRING, description = "Aspect to be checked") String aspectName) {

		Aspect aspect = Aspect.getAspect(aspectName.toLowerCase());
		Preconditions.checkNotNull(aspect, "Invalid aspect name");
		AspectList list = container.getAspects();
		if (list == null) return 0;
		return list.getAmount(aspect);
	}
}

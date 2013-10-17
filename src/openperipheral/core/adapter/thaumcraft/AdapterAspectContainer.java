package openperipheral.core.adapter.thaumcraft;

import java.util.ArrayList;
import java.util.HashMap;

import dan200.computer.api.IComputerAccess;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

public class AdapterAspectContainer implements IPeripheralAdapter {
	@Override
	public Class<?> getTargetClass() {
		return IAspectContainer.class;
	}
	
	@LuaMethod(returnType = LuaType.TABLE, description = "Get the Aspects stored in the block")
	public ArrayList<HashMap<String, Object>> getAspects(IComputerAccess computer, IAspectContainer container) {
		if (container == null) { return null; }
		AspectList aspectList = container.getAspects();
		if (aspectList == null || aspectList.size() == 0) { return null; }
		Aspect[] aspectArray = aspectList.getAspects();
		ArrayList<HashMap<String, Object>> aspectNames = new ArrayList<HashMap<String, Object>>(aspectArray.length);
		for (Aspect aspect : aspectArray) {
			HashMap<String, Object> aspectDetails = new HashMap<String, Object>(2);
			aspectDetails.put("name", aspect.getName());
			aspectDetails.put("quantity", aspectList.getAmount(aspect));
			aspectNames.add(aspectDetails);
		}
		return aspectNames;
	}
}

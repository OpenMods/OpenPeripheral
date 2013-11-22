package openperipheral.core.adapter;

import java.util.ArrayList;
import java.util.Map;

import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.core.AdapterManager;
import openperipheral.core.MethodDeclaration;
import openperipheral.core.util.MiscUtils;
import dan200.computer.api.IComputerAccess;

public class AdapterObject implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return Object.class;
	}

	@LuaMethod(returnType = LuaType.STRING, description = "List all the methods available")
	public String listMethods(IComputerAccess computer, Object object) {
		return MiscUtils.listMethods(getUniqueMethods(object));
	}

	@LuaMethod(returnType = LuaType.TABLE, description = "Get a complete table of information about all available methods")
	public Map<?, ?> getAdvancedMethodsData(IComputerAccess computer, Object object) {
		return MiscUtils.documentMethods(getUniqueMethods(object));
	}

	private ArrayList<MethodDeclaration> getUniqueMethods(Object object) {
		ArrayList<MethodDeclaration> methods = AdapterManager.getMethodsForTarget(object);
		ArrayList<MethodDeclaration> unique = new ArrayList<MethodDeclaration>();
		for (MethodDeclaration method : methods) {
			if (!unique.contains(method)) {
				unique.add(method);
			}
		}
		return unique;
	}

}

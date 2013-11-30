package openperipheral.adapter;

import java.util.List;
import java.util.Map;

import openperipheral.AdapterManager;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.peripheral.MethodDeclaration;
import openperipheral.util.PeripheralUtils;
import dan200.computer.api.IComputerAccess;

public class AdapterObject implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return Object.class;
	}

	@LuaMethod(returnType = LuaType.STRING, description = "List all the methods available")
	public String listMethods(IComputerAccess computer, Object object) {
		List<MethodDeclaration> methods = AdapterManager.getMethodsForTarget(object);
		return PeripheralUtils.listMethods(methods);
	}

	@LuaMethod(returnType = LuaType.TABLE, description = "Get a complete table of information about all available methods")
	public Map<?, ?> getAdvancedMethodsData(IComputerAccess computer, Object object) {
		List<MethodDeclaration> methods = AdapterManager.getMethodsForTarget(object);
		return PeripheralUtils.documentMethods(methods);
	}
}

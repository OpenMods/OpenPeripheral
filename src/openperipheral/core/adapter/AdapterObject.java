package openperipheral.core.adapter;

import java.util.ArrayList;
import java.util.Iterator;

import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.AdapterManager;
import openperipheral.core.MethodDeclaration;
import openperipheral.core.util.MiscUtils;
import dan200.computer.api.IComputerAccess;

public class AdapterObject implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return Object.class;
	}

	@LuaMethod
	public String listMethods(IComputerAccess computer, Object object) {
		return MiscUtils.documentMethods(AdapterManager.getMethodsForTarget(object));
	}

}

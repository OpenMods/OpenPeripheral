package openperipheral.core.integration.vanilla;

import java.util.ArrayList;
import java.util.Iterator;

import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.AdapterManager;
import openperipheral.core.MethodDeclaration;
import dan200.computer.api.IComputerAccess;

public class AdapterObject implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return Object.class;
	}

	@LuaMethod
	public String listMethods(IComputerAccess computer, Object object) {

		StringBuilder builder = new StringBuilder();

		ArrayList<MethodDeclaration> methods = AdapterManager.getMethodsForTarget(object);
		Iterator<MethodDeclaration> methodsIterator = methods.iterator();
		while (methodsIterator.hasNext()) {
			MethodDeclaration method = methodsIterator.next();
			builder.append(method.getLuaName());
			builder.append("(");
			builder.append(")");
			if (!methodsIterator.hasNext()) {
				break;
			}
			builder.append("\n");
		}

		return builder.toString();
	}

}

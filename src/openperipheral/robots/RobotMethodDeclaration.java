package openperipheral.robots;

import java.lang.reflect.Method;

import openperipheral.api.LuaMethod;
import openperipheral.core.MethodDeclaration;

public class RobotMethodDeclaration extends MethodDeclaration {
	
	public RobotMethodDeclaration(LuaMethod luaMethod, Method method,
			Object target) {
		super(luaMethod, method, target);
	}

	@Override
	public void initalize() {
		Class[] allParameters = method.getParameterTypes();
		requiredParameters = new Class[allParameters.length + 1];
	    System.arraycopy(allParameters, 0, requiredParameters, 1, allParameters.length);
	    requiredParameters[0] = int.class;
	}
}

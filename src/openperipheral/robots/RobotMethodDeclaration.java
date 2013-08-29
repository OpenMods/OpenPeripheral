package openperipheral.robots;

import java.lang.reflect.Method;

import openperipheral.api.LuaMethod;
import openperipheral.core.MethodDeclaration;

public class RobotMethodDeclaration extends MethodDeclaration {

	public RobotMethodDeclaration(LuaMethod luaMethod, Method method) {
		super(luaMethod, method, null);
	}

}

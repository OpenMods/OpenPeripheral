package openperipheral.core;

import java.lang.reflect.Method;

import openperipheral.api.LuaMethod;

public class MethodDeclaration {

	protected boolean isOnTick = false;
	protected Method method;
	protected Class[] requiredParameters;
	protected String luaName;
	protected Object target;

	public MethodDeclaration(LuaMethod luaMethod, Method method, Object target) {
		isOnTick = luaMethod.onTick();
		this.method = method;
		luaName = luaMethod.name();
		if (luaName.equals("[none set]")) {
			luaName = method.getName();
		}
		this.target = target;
		initalize();
	}

	public void initalize() {
		Class[] allParameters = method.getParameterTypes();
		requiredParameters = new Class[allParameters.length - 2];
		System.arraycopy(allParameters, 2, requiredParameters, 0,
				requiredParameters.length);
	}

	public boolean onTick() {
		return isOnTick;
	}

	public Method getMethod() {
		return method;
	}

	public Class[] getRequiredParameters() {
		return requiredParameters;
	}

	public String getLuaName() {
		return luaName;
	}

	public Object getTarget() {
		return target;
	}
}

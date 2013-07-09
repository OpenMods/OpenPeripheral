package openperipheral.core;

import java.lang.reflect.Method;

import org.bouncycastle.util.Arrays;

import openperipheral.api.LuaMethod;

public class MethodDeclaration {

	private boolean isOnTick = false;
	private Method method;
	private Class[] requiredParameters;
	private String luaName;
	
	public MethodDeclaration(LuaMethod luaMethod, Method method) {
		isOnTick = luaMethod.onTick();
		this.method = method;
		luaName = luaMethod.name();
		if (luaName.equals("[none set]")) {
			luaName = method.getName();
		}
		Class[] allParameters = method.getParameterTypes();
		requiredParameters = new Class[allParameters.length - 2];
	    System.arraycopy(allParameters, 2, requiredParameters, 0, requiredParameters.length);
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
}

package openperipheral;

import java.lang.reflect.Method;

import openperipheral.api.Arg;
import openperipheral.api.IAdapterBase;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class MethodDeclaration {

	protected boolean isOnTick = false;
	protected Method method;
	protected Class<?>[] requiredParameters;
	protected Class<?>[] requiredJavaParameters;
	protected String luaName;
	protected IAdapterBase targetAdapter;
	private LuaMethod luaMethod;

	public MethodDeclaration(LuaMethod luaMethod, Method method, IAdapterBase targetAdapter) {
		isOnTick = luaMethod.onTick();
		this.method = method;
		luaName = luaMethod.name();
		this.luaMethod = luaMethod;
		if (luaName.equals("[none set]")) {
			luaName = method.getName();
		}
		this.targetAdapter = targetAdapter;
		initalize();
	}

	public void initalize() {
		Class<?>[] allParameters = method.getParameterTypes();
		requiredJavaParameters = new Class[allParameters.length - 2];
		System.arraycopy(allParameters, 2, requiredJavaParameters, 0, requiredJavaParameters.length);
	}

	public boolean onTick() {
		return isOnTick;
	}

	public Method getMethod() {
		return method;
	}

	public Class<?>[] getRequiredJavaParameters() {
		return requiredJavaParameters;
	}

	public Arg[] getRequiredParameters() {
		return luaMethod.args();
	}

	public String getDescription() {
		return luaMethod.description();
	}

	public LuaType getReturnType() {
		return luaMethod.returnType();
	}

	public String getLuaName() {
		return luaName;
	}

	public IAdapterBase getTargetAdapter() {
		return targetAdapter;
	}
}

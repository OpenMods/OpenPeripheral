package openperipheral.common.definition;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import openperipheral.api.IMethodDefinition;
import openperipheral.api.IRestriction;
import openperipheral.api.LuaMethod;
import openperipheral.common.util.ReflectionHelper;

public class DefinitionLuaMethod implements IMethodDefinition {

	public static ArrayList<IMethodDefinition> getLuaMethodsForObject(Object target) {
		ArrayList<IMethodDefinition> methodDefinitions = new ArrayList<IMethodDefinition>();
		Method[] methods = target.getClass().getMethods();
		for (final Method method : methods) {
			if (method.isAnnotationPresent(LuaMethod.class)) {
				LuaMethod annotation = method.getAnnotation(LuaMethod.class);
				methodDefinitions.add(new DefinitionLuaMethod(method, annotation));
			}
		}
		return methodDefinitions;
	}
	
	private Class[] requiredParameters;
	private boolean isInstant;
	private String luaName;
	private String name;
	
	public DefinitionLuaMethod(Method method, LuaMethod annotation) {
		
		requiredParameters = method.getParameterTypes();
		isInstant = !annotation.onTick();
		name = method.getName();
		luaName = annotation.name();
		if (luaName.equals("[none set]")) {
			luaName = name;
		}
		
	}
	
	@Override
	public HashMap<Integer, String> getReplacements() {
		return null;
	}

	@Override
	public String getPostScript() {
		return null;
	}

	@Override
	public boolean getCauseTileUpdate() {
		return false;
	}

	@Override
	public Class[] getRequiredParameters() {
		return requiredParameters;
	}

	@Override
	public boolean isInstant() {
		return isInstant;
	}

	@Override
	public String getLuaName() {
		return luaName;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean needsSanitize() {
		return true;
	}

	@Override
	public ArrayList<IRestriction> getRestrictions(int index) {
		return null;
	}

	@Override
	public Object execute(Object target, Object[] args) throws Exception {
		return ReflectionHelper.callMethod("", target, new String[] { name }, args);
	}

}

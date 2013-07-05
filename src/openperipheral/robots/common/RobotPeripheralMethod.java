package openperipheral.robots.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import openperipheral.api.IRestriction;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.api.LuaMethod;
import openperipheral.core.definition.DefinitionLuaMethod;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;

/**
 * This class wraps a "Robot method" in a PeripheralMethodDefinition.
 * They're quite similar, except the peripheral method exposes additional
 * things that don't belong in the public API
 *
 */

public class RobotPeripheralMethod extends DefinitionLuaMethod implements IPeripheralMethodDefinition {
	
	private static HashMap<IRobotUpgradeProvider, ArrayList<IPeripheralMethodDefinition>> robotMethodCache = new HashMap<IRobotUpgradeProvider, ArrayList<IPeripheralMethodDefinition>>();

	public static ArrayList<IPeripheralMethodDefinition> getLuaMethodsForClass(Class klazz) {
		ArrayList<IPeripheralMethodDefinition> methodDefinitions = new ArrayList<IPeripheralMethodDefinition>();
		Method[] methods = klazz.getMethods();
		for (final Method method : methods) {
			if (method.isAnnotationPresent(LuaMethod.class)) {
				LuaMethod annotation = method.getAnnotation(LuaMethod.class);
				methodDefinitions.add(new RobotPeripheralMethod(method, annotation));
			}
		}
		return methodDefinitions;
	}
	
	public static ArrayList<IPeripheralMethodDefinition> getMethodsForProvider(IRobotUpgradeProvider provider) {
		if (!robotMethodCache.containsKey(provider)) {
			robotMethodCache.put(provider, RobotPeripheralMethod.getLuaMethodsForClass(provider.getUpgradeClass()));
		}
		return robotMethodCache.get(provider);
	}
	
	public RobotPeripheralMethod(Method method, LuaMethod annotation) {
		super(method, annotation);
	}

	/**
	 * Add the robot ID to the start of the required parameters. We don't
	 * really need upgrade methods to need to define that
	 */
	@Override
	public Class[] getRequiredParameters() {
		Class[] methodParams = super.getRequiredParameters();
		if (methodParams == null) {
			methodParams = new Class[]{};
		}
		Class[] paramsWithRobotId = new Class[methodParams.length + 1];
		paramsWithRobotId[0] = int.class;
		for (int i = 0; i < methodParams.length; i++) {
			paramsWithRobotId[i+1] = methodParams[i];
		}
		return paramsWithRobotId;
	}
}

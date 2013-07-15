package openperipheral.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import dan200.computer.api.IComputerAccess;

import openperipheral.api.IAdapterBase;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeAdapter;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.api.LuaMethod;
import openperipheral.robots.RobotMethodDeclaration;
import openperipheral.robots.RobotUpgradeManager;
import openperipheral.robots.block.TileEntityRobot;
import openperipheral.robots.upgrade.inventory.AdapterInventoryUpgrade;

public class AdapterManager {
	
	public static HashMap<Class, ArrayList<MethodDeclaration>> classList = new HashMap<Class, ArrayList<MethodDeclaration>>();
	
	public static void addPeripheralAdapter(IPeripheralAdapter adapter) {
		
		Class targetClass = adapter.getTargetClass();
		System.out.println("Enabling adapter " + adapter);
		try {
			if (targetClass != null) {
				
				for (Method method : adapter.getClass().getMethods()) {
					LuaMethod annotation = method.getAnnotation(LuaMethod.class);
					
					if (annotation != null) {
						
						Class[] parameters = method.getParameterTypes();
						
						if (parameters.length < 1 || !IComputerAccess.class.isAssignableFrom(parameters[0])) {
							throw new Exception(String.format("Parameter 1 of %s must be IComputerAccess", method.getName()));
						}
						if (parameters.length < 2 || !parameters[1].isAssignableFrom(targetClass)) {
							throw new Exception(String.format("Parameter 2 of %s must be a %s", method.getName(), targetClass.getSimpleName()));
						}
						if (annotation.args().length < parameters.length - 2) {
							throw new Exception(String.format("Not all of your method arguments are annotated for method %s/%s", adapter.getClass().getCanonicalName(), method.getName()));
						}
						
						if (!classList.containsKey(targetClass)) {
							classList.put(targetClass, new ArrayList<MethodDeclaration>());
						}
						
						classList.get(targetClass).add(new MethodDeclaration(annotation, method, adapter));
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void addRobotAdapter(Class< ? extends IRobotUpgradeAdapter> robotAdapter) {
		
		try {
			if (robotAdapter != null) {
				
				for (Method method : robotAdapter.getMethods()) {
					
					LuaMethod annotation = method.getAnnotation(LuaMethod.class);
					
					if (annotation != null) {
						
						Class[] parameters = method.getParameterTypes();
						
						if (parameters.length < 2) {
							throw new Exception("The first two parameters of a robot upgrade method should be an IComputerAccess and an IRobot");
						}
						
						if (!IComputerAccess.class.isAssignableFrom(parameters[0])) {
							throw new Exception(String.format("Parameter 1 of %s must be IComputerAccess", method.getName()));
						}

						if (annotation.args().length < parameters.length - 2) {
							throw new Exception(String.format("Not all of your method arguments are annotated for method %s/%s", robotAdapter.getCanonicalName(), method.getName()));
						}
						
						if (!classList.containsKey(robotAdapter)) {
							classList.put(robotAdapter, new ArrayList<MethodDeclaration>());
						}
						
						classList.get(robotAdapter).add(new RobotMethodDeclaration(annotation, method));
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static MethodDeclaration getMethodByName(String name) {
		for (ArrayList<MethodDeclaration> list : classList.values()) {
			for (MethodDeclaration dec : list) {
				if (dec.getLuaName().equals(name)) {
					return dec;
				}
			}
		}
		return null;
	}
	
	
	public static ArrayList<MethodDeclaration> getMethodsForClass(Class klazz) {
		
		HashMap<String, MethodDeclaration> methods = new HashMap<String, MethodDeclaration>();
		
		for (Entry<Class, ArrayList<MethodDeclaration>> entry : classList.entrySet()) {
			if (entry.getKey().isAssignableFrom(klazz)) {
				for (MethodDeclaration method : entry.getValue()) {
					if (!methods.containsKey(method.getLuaName())) {
						methods.put(method.getLuaName(), method);
					}
				}
			}
		}
		
		Collection<MethodDeclaration> collection = methods.values();
		
		return new ArrayList<MethodDeclaration>(collection);
	}

	public static ArrayList<MethodDeclaration> getMethodsForTarget(Object target) {
		return getMethodsForClass(target.getClass());
	}
}

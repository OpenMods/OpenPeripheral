package openperipheral.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;

public class AdapterManager {

	public static HashMap<Class, ArrayList<MethodDeclaration>> classList = new HashMap<Class, ArrayList<MethodDeclaration>>();
	
	public static void addPeripheralAdapter(IPeripheralAdapter peripheralAdapter) {
		Class targetClass = peripheralAdapter.getTargetClass();
		if (targetClass != null) {
			for (Method method : peripheralAdapter.getClass().getMethods()) {
				LuaMethod annotation = method.getAnnotation(LuaMethod.class);
				if (annotation != null) {
					if (!classList.containsKey(targetClass)) {
						classList.put(targetClass, new ArrayList<MethodDeclaration>());
					}
					classList.get(targetClass).add(new MethodDeclaration(annotation, method, peripheralAdapter));
				}
			}
		}
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

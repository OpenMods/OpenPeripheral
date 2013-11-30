package openperipheral;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.peripheral.MethodDeclaration;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import dan200.computer.api.IComputerAccess;

public class AdapterManager {

	private static Multimap<Class<?>, MethodDeclaration> classList = HashMultimap.create();

	public static void addPeripheralAdapter(IPeripheralAdapter adapter) {

		Class<?> targetClass = adapter.getTargetClass();
		System.out.println("Enabling adapter " + adapter);
		try {
			if (targetClass != null) {

				for (Method method : adapter.getClass().getMethods()) {
					LuaMethod annotation = method.getAnnotation(LuaMethod.class);

					if (annotation != null) {

						Class<?>[] parameters = method.getParameterTypes();

						if (parameters.length < 1 || !IComputerAccess.class.isAssignableFrom(parameters[0])) { throw new Exception(String.format("Parameter 1 of %s must be IComputerAccess", method.getName())); }
						if (parameters.length < 2 || !parameters[1].isAssignableFrom(targetClass)) { throw new Exception(String.format("Parameter 2 of %s must be a %s", method.getName(), targetClass.getSimpleName())); }
						if (annotation.args().length < parameters.length - 2) { throw new Exception(String.format("Not all of your method arguments are annotated for method %s/%s", adapter.getClass().getCanonicalName(), method.getName())); }

						classList.get(targetClass).add(new MethodDeclaration(annotation, method, adapter));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static MethodDeclaration getMethodByName(String name) {
		for (MethodDeclaration dec : classList.values()) {
			if (dec.getLuaName().equals(name)) return dec;
		}
		return null;
	}

	public static Set<Class<?>> getRegisteredClasses() {
		return classList.keySet();
	}

	public static List<MethodDeclaration> getMethodsForClass(Class<?> klazz) {
		Map<String, MethodDeclaration> methods = Maps.newHashMap();

		for (Class<?> cls : classList.keySet()) {
			if (cls.isAssignableFrom(klazz)) {
				for (MethodDeclaration method : classList.get(cls)) {
					if (!methods.containsKey(method.getLuaName())) {
						methods.put(method.getLuaName(), method);
					}
				}
			}
		}
		return ImmutableList.copyOf(methods.values());
	}

	public static List<MethodDeclaration> getMethodsForTarget(Object target) {
		return getMethodsForClass(target.getClass());
	}
}

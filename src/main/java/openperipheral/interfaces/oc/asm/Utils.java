package openperipheral.interfaces.oc.asm;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import com.google.common.collect.Maps;

public class Utils {

	public static String[] getInterfaces(Set<Class<?>> exposedInterfaces) {
		String[] result = new String[exposedInterfaces.size()];
		int i = 0;
		for (Class<?> cls : exposedInterfaces)
			result[i++] = Type.getInternalName(cls);

		return result;
	}

	public static Map<Method, Type> getExposedMethods(Collection<Class<?>> exposedInterfaces) {
		Map<Method, Type> result = Maps.newHashMap();

		for (Class<?> intf : exposedInterfaces) {
			Type intfType = Type.getType(intf);

			for (java.lang.reflect.Method m : intf.getMethods())
				result.put(Method.getMethod(m), intfType);
		}

		return result;
	}
}

package openperipheral.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.util.FileRetriever;
import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

public class AdapterManager {

	public static HashMap<Class, ArrayList<MethodDeclaration>> classList = new HashMap<Class, ArrayList<MethodDeclaration>>();
	
	public static void addPeripheralAdapter(IPeripheralAdapter peripheralAdapter) {
		Class targetClass = peripheralAdapter.getTargetClass();
		if (targetClass != null) {
			for (Method method : targetClass.getMethods()) {
				LuaMethod annotation = method.getAnnotation(LuaMethod.class);
				if (annotation != null) {
					if (!classList.containsKey(targetClass)) {
						classList.put(targetClass, new ArrayList<MethodDeclaration>());
					}
					classList.get(targetClass).add(new MethodDeclaration(annotation, method));
				}
			}
		}
	}

	public static ArrayList<MethodDeclaration> getMethodsForTarget(Object target) {
		ArrayList<MethodDeclaration> adapters = new ArrayList<MethodDeclaration>();
		for (Entry<Class, ArrayList<MethodDeclaration>> entry : classList.entrySet()) {
			if (entry.getKey().isAssignableFrom(target.getClass())) {
				adapters.addAll(entry.getValue());
			}
		}
		return adapters;
	}
}

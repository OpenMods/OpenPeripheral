package openperipheral.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import argo.jdom.JsonNode;

public class DefinitionClass {

	private String className;
	private ArrayList<DefinitionMethod> methods;
	
	private Class javaClass = null;
	
	public DefinitionClass(JsonNode json) {
		
		methods = new ArrayList<DefinitionMethod>();
		
		className = json.getStringValue("className");
		
		try {
			javaClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		if  (json.isNode("methods")) {
			for (JsonNode methodNode : json.getNode("methods").getElements()) {
				DefinitionMethod method = new DefinitionMethod(javaClass, methodNode);
				if (method.isValid()) {
					methods.add(method);
				}
			}
		}
	}
	
	public Class getJavaClass() {
		return javaClass;
	}

	public ArrayList<DefinitionMethod> getMethods() {
		return methods;
	}
}

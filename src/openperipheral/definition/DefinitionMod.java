package openperipheral.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import argo.jdom.JsonNode;

public class DefinitionMod {
	
	public String modId;
	public List<DefinitionClass> classes;

	public DefinitionMod(JsonNode json) {
		
		classes = new ArrayList<DefinitionClass>();
		
		modId = json.getStringValue("modId");
		
		if (json.isNode("classes")) {
			for (JsonNode classNode : json.getNode("classes").getElements()) {
				DefinitionClass klazzDef = new DefinitionClass(classNode);
				Class c = klazzDef.getJavaClass();
				if (c != null) {
					classes.add(new DefinitionClass(classNode));
				}
			}
		}
		
	}
	
	public String getModId() {
		return modId;
	}

	public Map<? extends Class, ? extends DefinitionClass> getValidClasses() {
		HashMap<Class, DefinitionClass> retClasses = new HashMap<Class, DefinitionClass>();
		for (DefinitionClass classDef : classes) {
			retClasses.put(classDef.getJavaClass(), classDef);
		}
		return retClasses;
	}

}

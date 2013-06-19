package openperipheral.common.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import openperipheral.api.IClassDefinition;
import openperipheral.api.IModDefinition;
import argo.jdom.JsonNode;

public class DefinitionJsonMod implements IModDefinition {

	public String modId;
	public List<DefinitionJsonClass> classes;

	public DefinitionJsonMod(JsonNode json) {

		classes = new ArrayList<DefinitionJsonClass>();

		modId = json.getStringValue("modId");

		if (json.isNode("classes")) {
			for (JsonNode classNode : json.getNode("classes").getElements()) {
				DefinitionJsonClass klazzDef = new DefinitionJsonClass(classNode);
				Class c = klazzDef.getJavaClass();
				if (c != null) {
					classes.add(new DefinitionJsonClass(classNode));
				}
			}
		}

	}

	@Override
	public String getModId() {
		return modId;
	}

	@Override
	public Map<? extends Class, ? extends IClassDefinition> getValidClasses() {
		HashMap<Class, DefinitionJsonClass> retClasses = new HashMap<Class, DefinitionJsonClass>();
		for (DefinitionJsonClass classDef : classes) {
			retClasses.put(classDef.getJavaClass(), classDef);
		}
		return retClasses;
	}

}

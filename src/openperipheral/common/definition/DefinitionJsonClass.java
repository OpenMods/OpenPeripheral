package openperipheral.common.definition;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.api.IMethodDefinition;
import argo.jdom.JsonNode;

public class DefinitionJsonClass implements IClassDefinition {

	private String className;
	private ArrayList<IMethodDefinition> methods;

	private Class javaClass = null;

	public DefinitionJsonClass(JsonNode json) {

		methods = new ArrayList<IMethodDefinition>();

		className = json.getStringValue("className");
		try {
			javaClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
		}

		if (javaClass != null && json.isNode("methods")) {
			for (JsonNode methodNode : json.getNode("methods").getElements()) {
				DefinitionJsonMethod method = new DefinitionJsonMethod(javaClass, methodNode);
				if (method.isValid()) {
					methods.add(method);
				}
			}
		}
	}

	@Override
	public Class getJavaClass() {
		return javaClass;
	}

	@Override
	public ArrayList<IMethodDefinition> getMethods(TileEntity tile) {
		return methods;
	}

}

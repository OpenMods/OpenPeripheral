package openperipheral.integration.thaumcraft;

import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaCallable;
import openperipheral.api.LuaType;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;

public class AdapterNode implements IPeripheralAdapter {
	private static final String NONE = "NONE";

	@Override
	public Class<?> getTargetClass() {
		return INode.class;
	}

	@LuaCallable(returnTypes = LuaType.STRING, description = "Get the type of the node")
	public String getNodeType(INode node) {
		NodeType nodeType = node.getNodeType();
		return (nodeType != null? nodeType.name() : NONE);
	}

	@LuaCallable(returnTypes = LuaType.STRING, description = "Get the modifier of the node")
	public String getNodeModifier(INode node) {
		NodeModifier nodeModifier = node.getNodeModifier();
		return (nodeModifier != null? nodeModifier.name() : NONE);
	}
}

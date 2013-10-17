package openperipheral.core.adapter.thaumcraft;

import dan200.computer.api.IComputerAccess;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;

public class AdapterNode implements IPeripheralAdapter {
	private static final String NONE = "NONE";
	
	public Class<?> getTargetClass() {
		return INode.class;
	}
	
	@LuaMethod(returnType = LuaType.STRING, description = "Get the type of the node")
	public String getNodeType(IComputerAccess computer, INode node) throws Exception {
		NodeType nodeType = node.getNodeType();
		return (nodeType != null ? nodeType.name() : NONE);
	}
	
	@LuaMethod(returnType = LuaType.STRING, description = "Get the modifier of the node")
	public String getNodeModifier(IComputerAccess computer, INode node) throws Exception {
		NodeModifier nodeModifier = node.getNodeModifier();
		return (nodeModifier != null ? nodeModifier.name() : NONE);
	}
}

package openperipheral.common.integration.sgcraft;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.api.IMethodDefinition;
import openperipheral.common.integration.sgcraft.method.SGCraftConnect;
import openperipheral.common.integration.sgcraft.method.SGCraftDisconnect;
import openperipheral.common.util.ReflectionHelper;

public class DefinitionBaseSGTileClass implements IClassDefinition {

	private Class klazz = null;
	
	private ArrayList<IMethodDefinition> methods = new ArrayList<IMethodDefinition>();
	
	public DefinitionBaseSGTileClass() {
		klazz = ReflectionHelper.getClass("gcewing.sg.SGBaseTE");
		methods.add(new SGCraftConnect());
		methods.add(new SGCraftDisconnect());
	}
	
	@Override
	public Class getJavaClass() {
		return klazz;
	}

	@Override
	public ArrayList<IMethodDefinition> getMethods(TileEntity tile) {
		return methods;
	}

}

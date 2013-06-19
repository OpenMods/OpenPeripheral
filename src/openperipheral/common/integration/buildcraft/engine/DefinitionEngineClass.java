package openperipheral.common.integration.buildcraft.engine;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.api.IMethodDefinition;
import openperipheral.common.util.ReflectionHelper;

public class DefinitionEngineClass implements IClassDefinition {

	private Class klazz = null;
	private ArrayList<IMethodDefinition> methods =  new ArrayList<IMethodDefinition>();
	
	public DefinitionEngineClass() {
		klazz = ReflectionHelper.getClass("buildcraft.energy.TileEngine");
		if (klazz != null) {
			methods.add(new DefinitionCurrentOutputMethod());
		}
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

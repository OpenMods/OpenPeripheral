package openperipheral.common.integration.buildcraft.engine;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.util.ReflectionHelper;

public class DefinitionEngineClass implements IClassDefinition {

	private Class klazz = null;
	private ArrayList<IPeripheralMethodDefinition> methods =  new ArrayList<IPeripheralMethodDefinition>();
	
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
	public ArrayList<IPeripheralMethodDefinition> getMethods(TileEntity tile) {
		return methods;
	}
}

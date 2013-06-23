package openperipheral.common.integration.forestry;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.api.IMethodDefinition;
import openperipheral.common.util.ReflectionHelper;

public class GenericBeeInfoClassDefinition implements IClassDefinition {

	private Class klazz = null;
	private ArrayList<IMethodDefinition> methods = new ArrayList<IMethodDefinition>();
	
	public GenericBeeInfoClassDefinition(String className) {
		klazz = ReflectionHelper.getClass(className);
		if (klazz != null) {
			methods.add(new GetBeeInfoMethod());
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

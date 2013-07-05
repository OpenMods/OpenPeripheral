package openperipheral.core.integration.forestry;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.util.ReflectionHelper;

public class GenericBeeInfoClassDefinition implements IClassDefinition {

	private Class klazz = null;
	private ArrayList<IPeripheralMethodDefinition> methods = new ArrayList<IPeripheralMethodDefinition>();
	
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
	public ArrayList<IPeripheralMethodDefinition> getMethods(TileEntity tile) {
		return methods;
	}

}

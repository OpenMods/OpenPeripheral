package openperipheral.common.integration.forestry;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.api.IMethodDefinition;
import openperipheral.common.util.ReflectionHelper;

public class BeeHousingClassDefinition implements IClassDefinition {

	private Class klazz = null;
	private ArrayList<IMethodDefinition> methods = new ArrayList<IMethodDefinition>();
	
	public BeeHousingClassDefinition() {
		klazz = ReflectionHelper.getClass("forestry.api.apiculture.IBeeHousing");
		if (klazz != null) {
			methods.add(new GetSpecificBeeMethod("getQueen"));
			methods.add(new GetSpecificBeeMethod("getDrone"));
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

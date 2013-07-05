package openperipheral.core.integration.forestry;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.util.ReflectionHelper;

public class BeeHousingClassDefinition implements IClassDefinition {

	private Class klazz = null;
	private ArrayList<IPeripheralMethodDefinition> methods = new ArrayList<IPeripheralMethodDefinition>();
	
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
	public ArrayList<IPeripheralMethodDefinition> getMethods(TileEntity tile) {
		return methods;
	}

}

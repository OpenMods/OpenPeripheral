package openperipheral.common.integration.appliedenergistics.gridinterface;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.util.ReflectionHelper;

public class DefinitionGridInterfaceClass implements IClassDefinition {

	private Class klazz = null;
	private ArrayList<IPeripheralMethodDefinition> methods = new ArrayList<IPeripheralMethodDefinition>();
	
	public DefinitionGridInterfaceClass() {
		klazz = ReflectionHelper.getClass("appeng.api.me.util.IGridInterface");
		if (klazz != null) {
			methods.add(new DefinitionRequestCraftingMethod());
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

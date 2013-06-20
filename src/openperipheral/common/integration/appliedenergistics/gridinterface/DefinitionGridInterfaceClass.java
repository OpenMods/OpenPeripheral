package openperipheral.common.integration.appliedenergistics.gridinterface;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.api.IMethodDefinition;
import openperipheral.common.util.ReflectionHelper;

public class DefinitionGridInterfaceClass implements IClassDefinition {

	private Class klazz = null;
	private ArrayList<IMethodDefinition> methods = new ArrayList<IMethodDefinition>();
	
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
	public ArrayList<IMethodDefinition> getMethods(TileEntity tile) {
		return methods;
	}

}

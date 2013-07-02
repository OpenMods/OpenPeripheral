package openperipheral.common.integration.gregtech;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.util.ReflectionHelper;

public abstract class DefinitionMetaClass implements IClassDefinition {

	private Class klazz;
	
	public DefinitionMetaClass() {
		klazz = ReflectionHelper.getClass("gregtechmod.api.metatileentity.BaseMetaTileEntity");
	}
	
	@Override
	public Class getJavaClass() {
		return klazz;
	}

	@Override
	public abstract ArrayList<IPeripheralMethodDefinition> getMethods(TileEntity tile);

	protected static Object getMetaTileEntity(TileEntity tile) {
		return ReflectionHelper.callMethod(false, "", tile, new String[] { "getMetaTileEntity" });
	}
}

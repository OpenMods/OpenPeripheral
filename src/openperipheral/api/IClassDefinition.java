package openperipheral.api;

import java.util.ArrayList;

import openperipheral.common.interfaces.IPeripheralMethodDefinition;

import net.minecraft.tileentity.TileEntity;

public interface IClassDefinition {

	public Class getJavaClass();
	public ArrayList<IPeripheralMethodDefinition> getMethods(TileEntity tile);

}

package openperipheral.api;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;

public interface IClassDefinition {

	public Class getJavaClass();
	public ArrayList<IPeripheralMethodDefinition> getMethods(TileEntity tile);

}

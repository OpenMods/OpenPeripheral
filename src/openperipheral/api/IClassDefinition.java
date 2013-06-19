package openperipheral.api;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;

public interface IClassDefinition {

	public Class getJavaClass();
	public ArrayList<IMethodDefinition> getMethods(TileEntity tile);

}

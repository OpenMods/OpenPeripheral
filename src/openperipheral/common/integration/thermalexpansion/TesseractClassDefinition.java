package openperipheral.common.integration.thermalexpansion;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.api.IMethodDefinition;
import openperipheral.common.util.ReflectionHelper;

public class TesseractClassDefinition implements IClassDefinition {

	private Class klazz = null;
	private ArrayList<IMethodDefinition> methods = new ArrayList<IMethodDefinition>();
	
	public TesseractClassDefinition() {
		klazz = ReflectionHelper.getClass("thermalexpansion.block.tesseract.TileTesseractRoot");
		if (klazz != null) {
			methods.add(new TesseractSetFrequencyMethodDefinition());
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

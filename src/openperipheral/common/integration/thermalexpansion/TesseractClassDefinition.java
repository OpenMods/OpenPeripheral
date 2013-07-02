package openperipheral.common.integration.thermalexpansion;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.util.ReflectionHelper;

public class TesseractClassDefinition implements IClassDefinition {

	private Class klazz = null;
	private ArrayList<IPeripheralMethodDefinition> methods = new ArrayList<IPeripheralMethodDefinition>();
	
	public TesseractClassDefinition() {
		klazz = ReflectionHelper.getClass("thermalexpansion.block.tesseract.TileTesseractRoot");
		if (klazz != null) {
			methods.add(new TesseractSetFrequencyMethodDefinition());
			methods.add(new TesseractSetModeMethodDefinition());
			methods.add(new TesseractGetModeMethodDefinition());
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

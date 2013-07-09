package openperipheral.core.integration.thermalexpansion;

import java.util.ArrayList;
import java.util.Arrays;

import dan200.computer.api.IComputerAccess;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.util.ReflectionHelper;

public class TesseractAdapter implements IPeripheralAdapter {

	private Class klazz = null;
	
	public TesseractAdapter() {
		klazz = ReflectionHelper.getClass("thermalexpansion.block.tesseract.TileTesseractRoot");
	}
	
	@Override
	public Class getTargetClass() {
		return klazz;
	}

	@LuaMethod
	public String getMode(IComputerAccess computer, Object target) {
		int mode = new Byte((Byte)(ReflectionHelper.getProperty("", target, "mode"))).intValue();
		return TEModule.tesseractModes[mode];
	}

	@LuaMethod
	public boolean setMode(IComputerAccess computer, Object target, String modeName) throws Exception {
		int mode = Arrays.asList(TEModule.tesseractModes).indexOf(modeName);
		if (mode == -1) {
			throw new Exception("Invalid mode specified");
		}
		ReflectionHelper.callMethod(false, "", target, new String[] { "removeFromRegistry" });
		ReflectionHelper.setProperty("", target, (byte)mode, "mode");
		ReflectionHelper.callMethod(false, "", target, new String[] { "addToRegistry" });
		return true;
	}
	
	@LuaMethod
	public Object setFrequency(IComputerAccess computer, Object target, int frequency) throws Exception {
		ReflectionHelper.callMethod(false, "", target, new String[] { "removeFromRegistry" });
		ReflectionHelper.setProperty("", target, frequency, "frequency");
		ReflectionHelper.callMethod(false, "", target, new String[] { "addToRegistry" });
		return true;
	}

}

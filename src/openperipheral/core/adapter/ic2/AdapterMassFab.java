package openperipheral.core.adapter.ic2;

import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.util.ReflectionHelper;
import dan200.computer.api.IComputerAccess;

public class AdapterMassFab implements IPeripheralAdapter {

	private Class klazz;
	
	public AdapterMassFab() {
		klazz = ReflectionHelper.getClass("ic2.core.block.machine.tileentity.TileEntityMatter");
	}
	
	@Override
	public Class getTargetClass() {
		return klazz;
	}
	
	@LuaMethod(onTick=false)
	public double getProgress(IComputerAccess computer, Object massfab) {
		int energy = (Integer) ReflectionHelper.getProperty("", massfab, "energy");
		double p = (double)energy / 10000;
	    if (p > 100)
	    {
	      p = 100;
	    }
	    return p;
	}

	@LuaMethod(onTick=false)
	public boolean isAmplificationAvailable(IComputerAccess computer, Object massfab) {
		return (Boolean) ReflectionHelper.getProperty("", massfab, "amplificationIsAvailable");
	}
	
}

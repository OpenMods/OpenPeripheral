package openperipheral.core.converter;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import openperipheral.api.ITypeConverter;

public class ConverterILiquidTank implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class required) {
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o instanceof IFluidTank) {
			IFluidTank t = (IFluidTank) o;
			Map map = new HashMap();
			map.put("capacity", t.getCapacity());
			map.put("amount", t.getFluidAmount());
			FluidStack lyqyd = t.getInfo().fluid;
			if (lyqyd != null) {
				map.put("id", lyqyd.fluidID);
				Fluid fluid = lyqyd.getFluid();
				map.put("name", fluid.getName());
				map.put("rawName", fluid.getLocalizedName());
			}
			return map;
		}
		return null;
	}

}

package openperipheral.core.converter;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import openperipheral.api.ITypeConverter;

public class ConverterFluidTankInfo implements ITypeConverter {

	@Override
	public Object fromLua(Object obj, Class expected) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o instanceof FluidTankInfo) {
			FluidTankInfo t = (FluidTankInfo) o;
			Map map = new HashMap();
			map.put("capacity", t.capacity);
			FluidStack lyqyd = t.fluid;
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

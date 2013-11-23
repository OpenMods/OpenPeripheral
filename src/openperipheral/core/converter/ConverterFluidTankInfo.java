package openperipheral.core.converter;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import openperipheral.api.ITypeConverter;

public class ConverterFluidTankInfo implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class<?> required) {
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object toLua(Object o) {
		if (o instanceof FluidTankInfo) {
			FluidTankInfo fti = (FluidTankInfo)o;
			Map map = new HashMap();
			map.put("capacity", fti.capacity);
			FluidStack fluidStack = fti.fluid;
			if (fluidStack != null) {
				map.put("amount", fluidStack.amount);
				map.put("id", fluidStack.fluidID);

				Fluid fluid = fluidStack.getFluid();
				if (fluid != null) {
					map.put("name", fluid.getName());
					map.put("rawName", fluid.getLocalizedName());
				}
			}
			return map;
		}
		return null;
	}

}

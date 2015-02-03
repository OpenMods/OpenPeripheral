package openperipheral.converter;

import java.util.Map;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import openperipheral.api.converter.IConverter;

import com.google.common.collect.Maps;

public class ConverterFluidStack extends GenericConverterAdapter {

	@Override
	public Object fromLua(IConverter registry, Object obj, Class<?> expected) {
		return null;
	}

	@Override
	public Object toLua(IConverter registry, Object obj) {
		if (obj instanceof FluidStack) {
			FluidStack fluidStack = (FluidStack)obj;
			Map<String, Object> result = Maps.newHashMap();
			result.put("amount", fluidStack.amount);
			result.put("id", fluidStack.fluidID);

			Fluid fluid = fluidStack.getFluid();
			if (fluid != null) {
				result.put("name", fluid.getName());
				result.put("rawName", fluid.getLocalizedName(fluidStack));
			}

			return result;
		}

		return null;
	}

}

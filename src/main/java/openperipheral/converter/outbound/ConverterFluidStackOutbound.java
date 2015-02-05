package openperipheral.converter.outbound;

import java.util.Map;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import openperipheral.api.converter.IConverter;
import openperipheral.api.helpers.SimpleOutboundConverter;

import com.google.common.collect.Maps;

public class ConverterFluidStackOutbound extends SimpleOutboundConverter<FluidStack> {

	@Override
	public Object convert(IConverter registry, FluidStack fluidStack) {
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

}

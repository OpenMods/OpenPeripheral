package openperipheral.converter.outbound;

import java.util.Map;

import net.minecraftforge.fluids.FluidTankInfo;
import openperipheral.api.converter.IConverter;
import openperipheral.api.helpers.SimpleOutboundConverter;

import com.google.common.collect.Maps;

public class ConverterFluidTankInfoOutbound extends SimpleOutboundConverter<FluidTankInfo> {

	@Override
	public Object convert(IConverter registry, FluidTankInfo fti) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("capacity", fti.capacity);
		map.put("contents", registry.fromJava(fti.fluid));
		return map;
	}
}

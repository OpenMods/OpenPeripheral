package openperipheral.converter;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fluids.FluidTankInfo;
import openperipheral.api.converter.IConverter;

public class ConverterFluidTankInfo extends GenericConverterAdapter {

	@Override
	public Object fromLua(IConverter registry, Object o, Class<?> required) {
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object toLua(IConverter registry, Object o) {
		if (o instanceof FluidTankInfo) {
			FluidTankInfo fti = (FluidTankInfo)o;
			Map map = new HashMap();
			map.put("capacity", fti.capacity);
			map.put("contents", registry.toLua(fti.fluid));
			return map;
		}
		return null;
	}
}

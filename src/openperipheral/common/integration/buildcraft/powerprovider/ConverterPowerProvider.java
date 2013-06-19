package openperipheral.common.integration.buildcraft.powerprovider;

import java.util.HashMap;

import openperipheral.api.ITypeConverter;
import buildcraft.api.power.IPowerProvider;

public class ConverterPowerProvider implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class required) {
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o instanceof IPowerProvider) {
			HashMap ret = new HashMap();
			IPowerProvider p = (IPowerProvider) o;
			ret.put("activationEnergy", p.getActivationEnergy());
			ret.put("latency", p.getLatency());
			ret.put("minEnergyReceived", p.getMinEnergyReceived());
			ret.put("maxEnergyReceived", p.getMaxEnergyReceived());
			ret.put("maxEnergyStored", p.getMaxEnergyStored());
			ret.put("energyStored", p.getEnergyStored());
			return ret;
		}
		return null;
	}

}

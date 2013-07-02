package openperipheral.common.integration.buildcraft.powerprovider;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.util.ReflectionHelper;

public class DefinitionPowerProviderClass implements IClassDefinition {

	private Class klazz = null;
	
	private ArrayList<IPeripheralMethodDefinition> methods = new ArrayList<IPeripheralMethodDefinition>();
	
	public DefinitionPowerProviderClass() {
		klazz = ReflectionHelper.getClass("buildcraft.api.power.IPowerReceptor");
		if (klazz != null) {
			methods.add(new DefinitionPowerProviderMethod("getActivationEnergy"));
			methods.add(new DefinitionPowerProviderMethod("getLatency"));
			methods.add(new DefinitionPowerProviderMethod("getMinEnergyReceived"));
			methods.add(new DefinitionPowerProviderMethod("getMaxEnergyReceived"));
			methods.add(new DefinitionPowerProviderMethod("getMaxEnergyStored"));
			methods.add(new DefinitionPowerProviderMethod("getEnergyStored"));
		}
	}
	
	@Override
	public Class getJavaClass() {
		return klazz;
	}

	@Override
	public ArrayList<IPeripheralMethodDefinition> getMethods(TileEntity tile) {
		return methods;
	}

}

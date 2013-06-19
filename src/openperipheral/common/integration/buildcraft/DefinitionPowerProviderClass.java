package openperipheral.common.integration.buildcraft;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IClassDefinition;
import openperipheral.api.IMethodDefinition;
import openperipheral.common.util.ReflectionHelper;

public class DefinitionPowerProviderClass implements IClassDefinition {

	private Class klazz = null;
	
	private ArrayList<IMethodDefinition> methods = new ArrayList<IMethodDefinition>();
	
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
	public ArrayList<IMethodDefinition> getMethods(TileEntity tile) {
		return methods;
	}

}

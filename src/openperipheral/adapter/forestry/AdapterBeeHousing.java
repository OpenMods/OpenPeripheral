package openperipheral.adapter.forestry;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.TypeConversionRegistry;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.Arg;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;

public class AdapterBeeHousing implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IBeeHousing.class;
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Can the bees breed?")
	public boolean canBreed(IComputerAccess computer, IBeeHousing beeHousing) {
		return beeHousing.canBreed();
	}

	@LuaMethod(returnType = LuaType.TABLE, description = "Get the drone")
	public IIndividual getDrone(IComputerAccess computer, IBeeHousing beeHousing) {
		ItemStack drone = beeHousing.getDrone();
		if (drone != null) { return AlleleManager.alleleRegistry.getIndividual(drone); }
		return null;
	}

	@LuaMethod(returnType = LuaType.TABLE, description = "Get the queen")
	public IIndividual getQueen(IComputerAccess computer, IBeeHousing beeHousing) {
		ItemStack queen = beeHousing.getQueen();
		if (queen != null) { return AlleleManager.alleleRegistry.getIndividual(queen); }
		return null;
	}

	/**
	 * Experimental method. Adding it aganist beehousing for now as we need some kind of block to run it against
	 * Trying to get the full breeding tree for all bees
	 * @param computer
	 * @param housing
	 * @return
	 */
	@LuaMethod(returnType = LuaType.TABLE, description="Get the full breeding list thingy. Experimental!")
	public Map<?, HashMap<String, Object>> getBeeBreedingData(IComputerAccess computer, IBeeHousing housing) {
		ISpeciesRoot beeRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
		if (beeRoot == null) {
			return null;
		}
		HashMap<Object, HashMap<String, Object>> result = new HashMap<Object, HashMap<String, Object>>();
		int j = 1;
		for (IMutation mutation : beeRoot.getMutations(false)) {
			HashMap<String, Object> mutationMap = new HashMap<String, Object>();
			IAllele allele1 = mutation.getAllele0();
			if (allele1 != null) {
				mutationMap.put("allele1", allele1.getName());
			}
			IAllele allele2 = mutation.getAllele1();
			if (allele2 != null) {
				mutationMap.put("allele2", allele2.getName());
			}
			mutationMap.put("chance", mutation.getBaseChance());
			mutationMap.put("specialConditions", TypeConversionRegistry.toLua(mutation.getSpecialConditions().toArray()));
			IAllele[] template = mutation.getTemplate();
			if (template != null && template.length > 0) {
				mutationMap.put("result", template[0].getName());
			}
			result.put(j++, mutationMap);
		}
		return result;
	}

	@LuaMethod(
			returnType = LuaType.TABLE,
			description="Get the parents for a particular mutation",
			args = {
					@Arg( name="childType", description="The type of bee you want the parents for", type=LuaType.STRING)
			})
	public Map<Object, HashMap<String, Object>> getBeeParents(IComputerAccess computer, IBeeHousing housing, String childType) {
		ISpeciesRoot beeRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
		if (beeRoot == null) {
			return null;
		}
		int i = 1;
		HashMap<Object, HashMap<String, Object>> result = new HashMap<Object, HashMap<String, Object>>();
		for (IMutation mutation : beeRoot.getMutations(false)) {
			IAllele[] template = mutation.getTemplate();
			if (template == null || template.length < 1) {
				continue;
			}
			if (template[0].getName().toLowerCase().equals(childType.toLowerCase())) {
				HashMap<String, Object> parentMap = new HashMap<String, Object>();
				IAllele allele1 = mutation.getAllele0();
				if (allele1 != null) {
					parentMap.put("allele1", allele1.getName());
				}
				IAllele allele2 = mutation.getAllele1();
				if (allele2 != null) {
					parentMap.put("allele2", allele2.getName());
				}
				parentMap.put("chance", mutation.getBaseChance());
				parentMap.put("specialConditions", TypeConversionRegistry.toLua(mutation.getSpecialConditions().toArray()));
				result.put(i++, parentMap);
			}
		}
		return result;
	}

}

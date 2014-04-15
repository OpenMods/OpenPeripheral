package openperipheral.integration.forestry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.api.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.*;

@OnTickSafe
public class AdapterBeeHousing implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IBeeHousing.class;
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Can the bees breed?")
	public boolean canBreed(IBeeHousing beeHousing) {
		return beeHousing.canBreed();
	}

	@LuaMethod(returnType = LuaType.TABLE, description = "Get the drone")
	public IIndividual getDrone(IBeeHousing beeHousing) {
		ItemStack drone = beeHousing.getDrone();
		if (drone != null) { return AlleleManager.alleleRegistry.getIndividual(drone); }
		return null;
	}

	@LuaMethod(returnType = LuaType.TABLE, description = "Get the queen")
	public IIndividual getQueen(IBeeHousing beeHousing) {
		ItemStack queen = beeHousing.getQueen();
		if (queen != null) { return AlleleManager.alleleRegistry.getIndividual(queen); }
		return null;
	}

	/**
	 * Experimental method. Adding it aganist beehousing for now as we need some
	 * kind of block to run it against
	 * Trying to get the full breeding tree for all bees
	 * 
	 * @param computer
	 * @param housing
	 * @return
	 */
	@LuaMethod(returnType = LuaType.TABLE, description = "Get the full breeding list thingy. Experimental!")
	public Map<Integer, Map<String, Object>> getBeeBreedingData(IBeeHousing housing) {
		ISpeciesRoot beeRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
		if (beeRoot == null) { return null; }
		Map<Integer, Map<String, Object>> result = Maps.newHashMap();
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
			mutationMap.put("specialConditions", mutation.getSpecialConditions().toArray());
			IAllele[] template = mutation.getTemplate();
			if (template != null && template.length > 0) {
				mutationMap.put("result", template[0].getName());
			}
			result.put(j++, mutationMap);
		}
		return result;
	}

	@LuaCallable(returnTypes = LuaType.TABLE, description = "Get all known bees mutations")
	public List<Map<String, String>> listAllSpecies(IBeeHousing housing) {
		ISpeciesRoot beeRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
		if (beeRoot == null) return null;
		List<Map<String, String>> result = Lists.newArrayList();

		for (IMutation mutation : beeRoot.getMutations(false)) {
			IAllele[] template = mutation.getTemplate();
			if (template != null && template.length > 0) {
				IAllele allele = template[0];
				if (allele instanceof IAlleleSpecies) result.add(serializeSpecies((IAlleleSpecies)allele));
			}
		}

		return result;
	}

	@LuaCallable(returnTypes = LuaType.TABLE, description = "Get the parents for a particular mutation")
	public List<Map<String, Object>> getBeeParents(IBeeHousing housing,
			@Arg(name = "childType", description = "The type of bee you want the parents for", type = LuaType.STRING) String childType) {
		ISpeciesRoot beeRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
		if (beeRoot == null) return null;
		List<Map<String, Object>> result = Lists.newArrayList();
		childType = childType.toLowerCase();

		for (IMutation mutation : beeRoot.getMutations(false)) {
			IAllele[] template = mutation.getTemplate();
			if (template == null || template.length < 1) continue;

			IAllele allele = template[0];

			if (!(allele instanceof IAlleleSpecies)) continue;

			IAlleleSpecies species = (IAlleleSpecies)allele;
			final String uid = species.getUID().toLowerCase();
			final String localizedName = species.getName().toLowerCase();

			if (localizedName.equals(childType) || uid.equals(childType)) {
				Map<String, Object> parentMap = serializeMutation(mutation);
				result.add(parentMap);
			}
		}
		return result;
	}

	private static Map<String, String> serializeSpecies(IAlleleSpecies species) {
		Map<String, String> result = Maps.newHashMap();
		result.put("name", species.getName());
		result.put("uid", species.getUID());
		return result;
	}

	private static Map<String, Object> serializeMutation(IMutation mutation) {
		Map<String, Object> parentMap = Maps.newHashMap();

		IAllele allele1 = mutation.getAllele0();
		if (allele1 instanceof IAlleleSpecies) parentMap.put("allele1", serializeSpecies((IAlleleSpecies)allele1));

		IAllele allele2 = mutation.getAllele1();
		if (allele2 instanceof IAlleleSpecies) parentMap.put("allele2", serializeSpecies((IAlleleSpecies)allele2));

		parentMap.put("chance", mutation.getBaseChance());
		parentMap.put("specialConditions", mutation.getSpecialConditions());
		return parentMap;
	}

}

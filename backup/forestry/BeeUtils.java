package openperipheral.core.integration.forestry;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.ISpeciesRoot;

public class BeeUtils {

	public static IBeeRoot beeRoot = null;

	public static boolean isBee(ItemStack itemstack) {
		if (itemstack == null)
			return false;
		try {
			getBeeRoot();
			if (beeRoot != null) {
				return beeRoot.isMember(itemstack);
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public static void getBeeRoot() {
		if (beeRoot == null) {
			ISpeciesRoot root = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
			if (root instanceof IBeeRoot) {
				beeRoot = (IBeeRoot) root;
			}
		}
	}

	public static HashMap beeToMap(ItemStack itemstack) {
		HashMap map = new HashMap();
		try {
			if (isBee(itemstack)) {

				if (beeRoot.isDrone(itemstack))
					map.put("Type", "Drone");
				else if (beeRoot.isMated(itemstack))
					map.put("Type", "Queen");
				else
					map.put("Type", "Prinsess");
				try {

					IBee thisBee = beeRoot.getMember(itemstack);

					if (thisBee.isAnalyzed()) {
						map.put("isAnalysed", true);
						map.put("isNatural", thisBee.isNatural());
						map.put("Generation", thisBee.getGeneration());
						map.put("Health", thisBee.getHealth());
						map.put("MaxHealth", thisBee.getMaxHealth());
						map.put("hasEffect", thisBee.hasEffect());
						IBeeGenome genome = thisBee.getGenome();
						map.put("getSpeed", genome.getSpeed());
						map.put("getLifespan", genome.getLifespan());
						map.put("getTolerantFlyer", genome.getTolerantFlyer());
						map.put("getCaveDwelling", genome.getCaveDwelling());
						map.put("getFertility", genome.getFertility());
						map.put("getNocturnal", genome.getNocturnal());
						map.put("Fertility", genome.getFertility());
						map.put("Flowering", genome.getFlowering());

					} else {
						map.put("isAnalysed", false);

					}
				} catch (Exception ibee) {
				}
			}
		} catch (Exception e) {

		}
		return map;
	}
}

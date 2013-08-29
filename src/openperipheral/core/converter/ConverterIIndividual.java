package openperipheral.core.converter;

import java.util.HashMap;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IBee;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import openperipheral.api.ITypeConverter;

public class ConverterIIndividual implements ITypeConverter {

	@Override
	public Object fromLua(Object obj, Class expected) {
		return null;
	}

	@Override
	public Object toLua(Object obj) {
		if (obj instanceof IIndividual) {
			HashMap map = new HashMap();
			IIndividual individual = (IIndividual) obj;
			map.put("displayName", individual.getDisplayName());
			map.put("ident", individual.getIdent());
			map.put("isAnalyzed", individual.isAnalyzed());
			map.put("isSecret", individual.isSecret());
			if (individual instanceof IBee) {
				IBee bee = (IBee) individual;
				map.put("canSpawn", bee.canSpawn());
				map.put("generation", bee.getGeneration());
				map.put("health", bee.getHealth());
				map.put("maxHealth", bee.getMaxHealth());
				map.put("hasEffect", bee.hasEffect());
				map.put("isAlive", bee.isAlive());
				map.put("isIrregularMating", bee.isIrregularMating());
				map.put("isNatural", bee.isNatural());

				if (individual.isAnalyzed()) {
					IGenome genome = individual.getGenome();
					HashMap active = new HashMap();
					HashMap inactive = new HashMap();
					active.put("species", genome.getActiveAllele(EnumBeeChromosome.SPECIES.ordinal()).getName());
					inactive.put("species", genome.getInactiveAllele(EnumBeeChromosome.SPECIES.ordinal()).getName());

					active.put("caveDwelling", ((IAlleleBoolean)genome.getActiveAllele(EnumBeeChromosome.CAVE_DWELLING.ordinal())).getValue());
					inactive.put("caveDwelling", ((IAlleleBoolean)genome.getInactiveAllele(EnumBeeChromosome.CAVE_DWELLING.ordinal())).getValue());
					
					active.put("effect", genome.getActiveAllele(EnumBeeChromosome.EFFECT.ordinal()).getName());
					inactive.put("effect", genome.getInactiveAllele(EnumBeeChromosome.EFFECT.ordinal()).getName());
					
					active.put("fertility", ((IAlleleInteger)genome.getActiveAllele(EnumBeeChromosome.FERTILITY.ordinal())).getValue());
					inactive.put("fertility", ((IAlleleInteger)genome.getInactiveAllele(EnumBeeChromosome.FERTILITY.ordinal())).getValue());

					active.put("flowerProvider", ((IAlleleFlowers)genome.getActiveAllele(EnumBeeChromosome.FLOWER_PROVIDER.ordinal())).getProvider().getDescription());
					inactive.put("flowerProvider", ((IAlleleFlowers)genome.getInactiveAllele(EnumBeeChromosome.FLOWER_PROVIDER.ordinal())).getProvider().getDescription());

					active.put("flowering", genome.getActiveAllele(EnumBeeChromosome.FLOWERING.ordinal()).getName());
					inactive.put("flowering", genome.getInactiveAllele(EnumBeeChromosome.FLOWERING.ordinal()).getName());

					active.put("humidityTolerance", ((IAlleleTolerance)genome.getActiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal())).getValue());
					inactive.put("humidityTolerance", ((IAlleleTolerance)genome.getInactiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal())).getValue());

					active.put("lifespan", genome.getActiveAllele(EnumBeeChromosome.LIFESPAN.ordinal()).getName());
					inactive.put("lifespan", genome.getInactiveAllele(EnumBeeChromosome.LIFESPAN.ordinal()).getName());

					active.put("nocturnal", ((IAlleleBoolean)genome.getActiveAllele(EnumBeeChromosome.NOCTURNAL.ordinal())).getValue());
					inactive.put("nocturnal", ((IAlleleBoolean)genome.getInactiveAllele(EnumBeeChromosome.NOCTURNAL.ordinal())).getValue());

				    IAlleleSpecies primary = individual.getGenome().getPrimary();
				    IAlleleSpecies secondary = individual.getGenome().getSecondary();
				    active.put("species", primary.getName());
				    inactive.put("species", secondary.getName());
				    
					map.put("active", active);
					map.put("inactive", inactive);
				}
			}
			return map;
		}
		return null;
	}

}

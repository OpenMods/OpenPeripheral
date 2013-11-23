package openperipheral.converter;

import java.util.HashMap;

import openperipheral.api.ITypeConverter;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IBee;
import forestry.api.genetics.IAlleleArea;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;

public class ConverterIIndividual implements ITypeConverter {

	@Override
	public Object fromLua(Object obj, Class<?> expected) {
		return null;
	}

	@Override
	public Object toLua(Object obj) {
		if (obj instanceof IIndividual) {
			HashMap<Object, Object> map = new HashMap<Object, Object>();
			IIndividual individual = (IIndividual)obj;
			map.put("displayName", individual.getDisplayName());
			map.put("ident", individual.getIdent());
			map.put("isAnalyzed", individual.isAnalyzed());
			map.put("isSecret", individual.isSecret());
			if (individual instanceof IBee) {
				IBee bee = (IBee)individual;
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
					HashMap<Object, Object> active = new HashMap<Object, Object>();
					HashMap<Object, Object> inactive = new HashMap<Object, Object>();
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

					active.put("humidityTolerance", ((IAlleleTolerance)genome.getActiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal())).getValue().toString());
					inactive.put("humidityTolerance", ((IAlleleTolerance)genome.getInactiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal())).getValue().toString());

					active.put("lifespan", genome.getActiveAllele(EnumBeeChromosome.LIFESPAN.ordinal()).getName());
					inactive.put("lifespan", genome.getInactiveAllele(EnumBeeChromosome.LIFESPAN.ordinal()).getName());

					active.put("nocturnal", ((IAlleleBoolean)genome.getActiveAllele(EnumBeeChromosome.NOCTURNAL.ordinal())).getValue());
					inactive.put("nocturnal", ((IAlleleBoolean)genome.getInactiveAllele(EnumBeeChromosome.NOCTURNAL.ordinal())).getValue());

					active.put("speed", genome.getActiveAllele(EnumBeeChromosome.SPEED.ordinal()).getName());
					inactive.put("speed", genome.getInactiveAllele(EnumBeeChromosome.SPEED.ordinal()).getName());

					active.put("temperatureTolerance", ((IAlleleTolerance)genome.getActiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal())).getValue().name());
					inactive.put("temperatureTolerance", ((IAlleleTolerance)genome.getInactiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal())).getValue().name());

					int[] area = ((IAlleleArea)genome.getActiveAllele(EnumBeeChromosome.TERRITORY.ordinal())).getValue();
					active.put("territory", area[0] + ","+area[1]+","+area[2]);
					area = ((IAlleleArea)genome.getInactiveAllele(EnumBeeChromosome.TERRITORY.ordinal())).getValue();
					inactive.put("territory", area[0] + ","+area[1]+","+area[2]);

					active.put("tolerantFlyer", genome.getActiveAllele(EnumBeeChromosome.TOLERANT_FLYER.ordinal()).isDominant());
					inactive.put("tolerantFlyer", genome.getInactiveAllele(EnumBeeChromosome.TOLERANT_FLYER.ordinal()).isDominant());

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

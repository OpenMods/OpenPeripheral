package forestry.api.genetics;

import java.util.EnumSet;

import net.minecraftforge.common.EnumPlantType;

public interface IPollinatable {

	/**
	 * @return plant types this pollinatable is classified as. (Can be used by bees to determine whether to interact or not.
	 */
	EnumSet<EnumPlantType> getPlantType();

	/**
	 * @return IIndividual containing the genetic information of this IPollinatable
	 */
	IIndividual getPollen();

	/**
	 * Checks whether this {@IPollinatable} can mate with the given pollen.
	 * 
	 * Must be the one to check genetic equivalency.
	 * 
	 * @param pollen
	 *            IIndividual representing the pollen.
	 * @return true if mating is possible, false otherwise.
	 */
	boolean canMateWith(IIndividual pollen);

	/**
	 * Pollinates this entity.
	 * 
	 * @param pollen
	 *            IIndividual representing the pollen.
	 */
	void mateWith(IIndividual pollen);

}

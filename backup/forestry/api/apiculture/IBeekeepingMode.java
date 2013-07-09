package forestry.api.apiculture;

import java.util.ArrayList;

import net.minecraft.world.World;

public interface IBeekeepingMode extends IBeeModifier {

	/**
	 * @return Localized name of this beekeeping mode.
	 */
	String getName();

	/**
	 * @return Localized list of strings outlining the behaviour of this beekeeping mode.
	 */
	ArrayList<String> getDescription();

	/**
	 * @return Float used to modify the wear on comb frames.
	 */
	float getWearModifier();

	/**
	 * @param queen
	 * @return fertility taking into account the birthing queen and surroundings.
	 */
	int getFinalFertility(IBee queen, World world, int x, int y, int z);

	/**
	 * @param queen
	 * @return true if the queen is genetically "fatigued" and should not be reproduced anymore.
	 */
	boolean isFatigued(IBee queen);

	/**
	 * @param queen
	 * @param housing
	 * @return true if the queen is being overworked in the bee housing (with chance). will trigger a negative effect.
	 */
	boolean isOverworked(IBee queen, IBeeHousing housing);

	/**
	 * 
	 * @param queen
	 * @param offspring
	 * @param housing
	 * @return true if the genetic structure of the queen is breaking down during spawning of the offspring (with chance). will trigger a negative effect.
	 */
	boolean isDegenerating(IBee queen, IBee offspring, IBeeHousing housing);

	/**
	 * @param queen
	 * @return true if an offspring of this queen is considered a natural
	 */
	boolean isNaturalOffspring(IBee queen);

	/**
	 * @param queen
	 * @return true if this mode allows the passed queen or princess to be multiplied
	 */
	boolean mayMultiplyPrincess(IBee queen);


}

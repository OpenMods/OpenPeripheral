package erogenousbeef.bigreactors.api;

public interface IHeatEntity {	
	/**
	 * Returns the amount of heat in the entity, in celsius.
	 * @return The amount of heat in the entity, in celsius.
	 */
	public float getHeat();
	
	/**
	 * The thermal conductivity of the entity.
	 * This is the percentage of the available heat difference
	 * that will be absorbed in one tick, with 1 being 100%.
	 * Numbers over 1 are ignored.
	 * @return Thermal conductivity constant, the percentage heat difference to absorb in 1 sec
	 */
	public float getThermalConductivity();
}

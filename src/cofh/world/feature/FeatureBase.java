package cofh.world.feature;

import java.util.HashSet;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import cofh.api.world.IFeatureGenerator;

public abstract class FeatureBase implements IFeatureGenerator {

	final String name;
	final byte type;
	final boolean regen;
	final HashSet biomes = new HashSet<String>();

	public FeatureBase(String name, boolean regen) {

		this.name = name;
		this.type = 0;
		this.regen = regen;
	}

	public FeatureBase(String name, WorldGenerator worldGen, byte type, boolean regen) {

		this.name = name;
		this.type = type;
		this.regen = regen;
	}

	/* IFeatureGenerator */
	@Override
	public final String getFeatureName() {

		return name;
	}

	@Override
	public abstract boolean generateFeature(Random random, int chunkX, int chunkZ, World world, boolean newGen);

}

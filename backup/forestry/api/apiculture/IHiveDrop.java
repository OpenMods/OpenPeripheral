package forestry.api.apiculture;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Bees can be seeded either as hive drops or as mutation results.
 * 
 * Add IHiveDrops to BeeManager.hiveDrops
 * 
 * @author SirSengir
 */
public interface IHiveDrop {

	ItemStack getPrincess(World world, int x, int y, int z, int fortune);

	Collection<ItemStack> getDrones(World world, int x, int y, int z, int fortune);

	Collection<ItemStack> getAdditional(World world, int x, int y, int z, int fortune);

	/**
	 * Chance to drop. Default drops have 80 (= 80 %).
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	int getChance(World world, int x, int y, int z);
}

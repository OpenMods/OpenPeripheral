package mods.railcraft.api.core;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

/**
 * If you want your block to connect (or not connect) to posts,
 * implement this interface.
 *
 * The result takes priority over any other rules.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IPostConnection
{

    /**
     * Return true if the block at this location should connect to a post.
     * @param world The World
     * @param x x-Coord
     * @param y y-Coord
     * @param z z-Coord
     * @param side Side to connect to
     * @return true if connect
     */
    public boolean connectsAt(IBlockAccess world, int x, int y, int z, ForgeDirection side);
}

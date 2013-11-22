package openperipheral.core.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class BlockUtils {
	
	public static TileEntity getTileInDirection(TileEntity tile, ForgeDirection direction) {
		int targetX = tile.xCoord + direction.offsetX;
		int targetY = tile.yCoord + direction.offsetY;
		int targetZ = tile.zCoord + direction.offsetZ;
		return tile.worldObj.getBlockTileEntity(targetX, targetY, targetZ);
	}
}

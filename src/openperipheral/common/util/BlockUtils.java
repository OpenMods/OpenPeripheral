package openperipheral.common.util;

import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;

public class BlockUtils {
	
	public static ForgeDirection get2dOrientation(Vec3 pos1, Vec3 pos2) {
		
		double Dx = pos1.xCoord - pos2.xCoord;
		double Dz = pos1.zCoord - pos2.zCoord;
		double angle = Math.atan2(Dz, Dx) / Math.PI * 180 + 180;

		if (angle < 45 || angle > 315)
			return ForgeDirection.EAST;
		else if (angle < 135)
			return ForgeDirection.SOUTH;
		else if (angle < 225)
			return ForgeDirection.WEST;
		else
			return ForgeDirection.NORTH;
	
	}
	
	public static ForgeDirection get3dOrientation(Vec3 pos1, Vec3 pos2) {
		double Dx = pos1.xCoord - pos2.xCoord;
		double Dy = pos1.yCoord - pos2.yCoord;
		double angle = Math.atan2(Dy, Dx) / Math.PI * 180 + 180;

		if (angle > 45 && angle < 135) {
			return ForgeDirection.UP;
		} else if (angle > 225 && angle < 315) {
			return ForgeDirection.DOWN;
		} else {
			return get2dOrientation(pos1, pos2);
		}
	}
}

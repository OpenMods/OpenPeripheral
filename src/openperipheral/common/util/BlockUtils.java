package openperipheral.common.util;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockUtils {
	
	public static ForgeDirection get2dOrientation(EntityLiving entity) {
		int l = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
	    switch (l) { case 0:
	      return ForgeDirection.SOUTH;
	    case 1:
	      return ForgeDirection.WEST;
	    case 2:
	      return ForgeDirection.NORTH;
	    case 3:
	      return ForgeDirection.EAST;
	    }
	    return ForgeDirection.SOUTH;
	
	}
	
	public static ForgeDirection get3dOrientation(EntityLiving entity) {
		if (entity.rotationPitch > 66.5F)
        {
          return ForgeDirection.DOWN;
        }
        else if (entity.rotationPitch < -66.5F)
        {
            return ForgeDirection.UP;
        }
		return get2dOrientation(entity);
	}
	
	public static void dropItemStackInWorld(World worldObj, int x, int y, int z, ItemStack stack) {
		float f = 0.7F;
        double d0 = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        double d1 = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        double d2 = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        EntityItem entityitem = new EntityItem(worldObj, (double)x + d0, (double)y + d1, (double)z + d2, stack);
        entityitem.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityitem);
	}
}

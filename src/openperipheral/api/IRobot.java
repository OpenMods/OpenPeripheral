package openperipheral.api;

import net.minecraft.entity.EntityCreature;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public interface IRobot {
	public float getMoveSpeed();
	public void setMoveSpeed(float speed);
	public EntityCreature getEntity();
	public TileEntity getController();
	public Vec3 getLocation();
	public World getWorld();
	public float getEyeHeight();
	public float getFuelLevel();
	public boolean hasFuel();
	public void setFuelLevel(float fuel);
	public void modifyFuelLevel(float fuel);
	public void setPitch(float pitch);
	public float getPitch();
	public void setYaw(float yaw);
	public float getYaw();
	public void setMaxHealth(int maxHealth);
	public int getMaxHealth();
	public void fireEvent(String eventName, Object[] args);
	public void fireEvent(String eventName);
	public boolean isLinkedToController();
	public IInventory getInventory();
	public MovingObjectPosition getLookingAt();
	public EnumRobotType getRobotType();
}

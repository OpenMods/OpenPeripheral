package openperipheral.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public interface ISensorEnvironment {

	public boolean isTurtle();
	public Vec3 getLocation();
	public World getWorld();
	public ItemStack getSensorCardStack();

}

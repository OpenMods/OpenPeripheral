package dan200.turtle.api;

import dan200.computer.api.IHostedPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract interface ITurtleAccess
{
  public abstract World getWorld();

  public abstract Vec3 getPosition();

  public abstract Vec3 getVisualPosition(float paramFloat);

  public abstract int getFacingDir();

  public abstract int getInventorySize();

  public abstract int getSelectedSlot();

  public abstract ItemStack getSlotContents(int paramInt);

  public abstract void setSlotContents(int paramInt, ItemStack paramItemStack);

  public abstract boolean storeItemStack(ItemStack paramItemStack);

  public abstract boolean dropItemStack(ItemStack paramItemStack, int paramInt);

  public abstract boolean deployWithItemStack(ItemStack paramItemStack, int paramInt);

  public abstract boolean attackWithItemStack(ItemStack paramItemStack, int paramInt, float paramFloat);

  public abstract int getFuelLevel();

  public abstract boolean refuelWithItemStack(ItemStack paramItemStack);

  public abstract boolean consumeFuel(int paramInt);

  public abstract int issueCommand(ITurtleCommandHandler paramITurtleCommandHandler);

  public abstract ITurtleUpgrade getUpgrade(TurtleSide paramTurtleSide);

  public abstract IHostedPeripheral getPeripheral(TurtleSide paramTurtleSide);
}

/* Location:           C:\Users\mikeef\Documents\OpenPeripheral_161\forge\mcp\jars\mods\ComputerCraft\
 * Qualified Name:     dan200.turtle.api.ITurtleAccess
 * JD-Core Version:    0.6.2
 */
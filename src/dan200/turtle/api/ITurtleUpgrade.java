package dan200.turtle.api;

import dan200.computer.api.IHostedPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public abstract interface ITurtleUpgrade
{
  public abstract int getUpgradeID();

  public abstract String getAdjective();

  public abstract TurtleUpgradeType getType();

  public abstract ItemStack getCraftingItem();

  public abstract boolean isSecret();

  public abstract IHostedPeripheral createPeripheral(ITurtleAccess paramITurtleAccess, TurtleSide paramTurtleSide);

  public abstract boolean useTool(ITurtleAccess paramITurtleAccess, TurtleSide paramTurtleSide, TurtleVerb paramTurtleVerb, int paramInt);

  public abstract Icon getIcon(ITurtleAccess paramITurtleAccess, TurtleSide paramTurtleSide);
}

/* Location:           C:\Users\mikeef\Documents\OpenPeripheral_161\forge\mcp\jars\mods\ComputerCraft\
 * Qualified Name:     dan200.turtle.api.ITurtleUpgrade
 * JD-Core Version:    0.6.2
 */
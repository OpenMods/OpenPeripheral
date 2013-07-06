package dan200.computer.api;

public abstract interface IPeripheral
{
  public abstract String getType();

  public abstract String[] getMethodNames();

  public abstract Object[] callMethod(IComputerAccess paramIComputerAccess, int paramInt, Object[] paramArrayOfObject)
    throws Exception;

  public abstract boolean canAttachToSide(int paramInt);

  public abstract void attach(IComputerAccess paramIComputerAccess);

  public abstract void detach(IComputerAccess paramIComputerAccess);
}

/* Location:           C:\Users\mikeef\Documents\OpenPeripheral_161\forge\mcp\jars\mods\ComputerCraft\
 * Qualified Name:     dan200.computer.api.IPeripheral
 * JD-Core Version:    0.6.2
 */
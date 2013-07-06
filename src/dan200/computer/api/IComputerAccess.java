package dan200.computer.api;

public abstract interface IComputerAccess
{
  public abstract int createNewSaveDir(String paramString);

  public abstract String mountSaveDir(String paramString1, String paramString2, int paramInt, boolean paramBoolean, long paramLong);

  public abstract String mountFixedDir(String paramString1, String paramString2, boolean paramBoolean, long paramLong);

  public abstract void unmount(String paramString);

  public abstract int getID();

  public abstract void queueEvent(String paramString, Object[] paramArrayOfObject);

  public abstract String getAttachmentName();
}

/* Location:           C:\Users\mikeef\Documents\OpenPeripheral_161\forge\mcp\jars\mods\ComputerCraft\
 * Qualified Name:     dan200.computer.api.IComputerAccess
 * JD-Core Version:    0.6.2
 */
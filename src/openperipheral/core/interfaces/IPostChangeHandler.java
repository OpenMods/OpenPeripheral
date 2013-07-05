package openperipheral.core.interfaces;


public interface IPostChangeHandler {
	public void execute(Object tile, IPeripheralMethodDefinition luaMethod, Object[] values);
}

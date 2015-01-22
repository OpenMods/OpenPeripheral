package openperipheral.api;

/**
 * Used for returning multiple objects back to lua. Each object will
 * be individually passed through the type converters into a Lua friendly
 * format e.g. local x, y, z = p.getLocation()
 *
 * @see MultipleReturn
 *
 * @author mikeef
 *
 */
public interface IMultiReturn {
	public Object[] getObjects();
}

package openperipheral;

public interface ITypeConverter {
	public Object fromLua(Object o, Class required);
	public Object toLua(Object o);
}

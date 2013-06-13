package openperipheral.common.util;

public class ReflectionWrapper {

	private Object target;
	public ReflectionWrapper(Object target) {
		this.target = target;
	}
	
	public Object getRaw() {
		return target;
	}
	
	public ReflectionWrapper call(boolean replace, String methodName, Object ... args) {
		return new ReflectionWrapper(ReflectionHelper.callMethod(replace, "", target, new String[] { methodName }, args));
	}
	
	public ReflectionWrapper call(String[] methodNames, Object ... args) {
		return new ReflectionWrapper(ReflectionHelper.callMethod("", target, methodNames, args));
	}
	
	public Object get(String[] fieldNames) {
		return new ReflectionWrapper(ReflectionHelper.getProperty("", target, fieldNames));
	}

	public Object get(String fieldName) {
		return new ReflectionWrapper(ReflectionHelper.getProperty("", target, new String[] { fieldName }));
	}
	
	public void set(String fieldName, Object value) {
		ReflectionHelper.setProperty("", target, value, new String[] { fieldName });
	}

	public void set(String[] fieldNames, Object value) {
		ReflectionHelper.setProperty("", target, value, fieldNames);
	}
}

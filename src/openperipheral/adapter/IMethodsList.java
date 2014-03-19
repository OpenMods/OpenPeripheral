package openperipheral.adapter;

import java.util.List;

public interface IMethodsList<E extends IMethodExecutor> {

	public abstract List<E> getMethods();

	public abstract Class<?> getTargetClass();

	public abstract String describeType();

}
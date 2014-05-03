package openperipheral.adapter;

import java.util.Collection;

public interface IMethodsHolder<E extends IMethodExecutor> {
	public Collection<E> listMethods();
}

package openperipheral.interfaces.oc.providers;

import com.google.common.base.Throwables;
import openmods.injector.IClassBytesProvider;
import openperipheral.adapter.composed.ComposedMethodsFactory;
import openperipheral.util.NameUtils;

public class EnvironmentClassBytesProvider<T> implements IClassBytesProvider {

	private final ComposedMethodsFactory<IEnviromentInstanceWrapper<T>> factory;

	public EnvironmentClassBytesProvider(ComposedMethodsFactory<IEnviromentInstanceWrapper<T>> factory) {
		this.factory = factory;
	}

	@Override
	public byte[] getClassBytes(String fullClassName, String arg) {
		/**
		 * Now, this may seem terribly convoluted. Why just not inject via ClassLoader#defineClass?
		 * Well, that was initial plan, but then I wanted to have class generation directly in class loader,
		 * so OC deserializer can at least partially work (i.e. return non-functional but valid instance instead of erroring)
		 */
		String clsName = NameUtils.degrumize(arg);

		try {
			Class<?> targetCls = Class.forName(clsName);

			IEnviromentInstanceWrapper<T> wrapper = factory.getAdaptedClass(targetCls);
			return wrapper.isEmpty()? null : wrapper.getClassBytes();
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}
	}

}

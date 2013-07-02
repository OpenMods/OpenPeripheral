package openperipheral.common.drawable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import openperipheral.common.converter.TypeConversionRegistry;
import openperipheral.common.interfaces.IDrawable;
import openperipheral.common.tileentity.TileEntityGlassesBridge;
import openperipheral.common.util.ReflectionHelper;

public abstract class BaseDrawable implements IDrawable {

	protected String[] methodNames;

	private boolean deleted = false;
	protected byte zIndex = 0;

	private WeakReference<TileEntityGlassesBridge> bridge;

	public BaseDrawable() {
	}

	public BaseDrawable(TileEntityGlassesBridge _bridge) {
		bridge = new WeakReference<TileEntityGlassesBridge>(_bridge);
	}

	@Override
	public String[] getMethodNames() {
		return methodNames;
	}

	@Override
	public Object[] callMethod(int methodId, Object[] arguments) throws Exception {

		if (deleted) {
			return null;
		}

		Method method = ReflectionHelper.getMethod(this.getClass(), new String[] { methodNames[methodId] }, arguments.length);

		ArrayList<Object> args = new ArrayList(Arrays.asList(arguments));

		if (method == null) {
			throw new Exception("Invalid number of arguments");
		}

		Class[] requiredParameters = method.getParameterTypes();

		for (int i = 0; i < requiredParameters.length; i++) {
			Object converted = TypeConversionRegistry.fromLua(args.get(i), requiredParameters[i]);
			if (converted == null) {
				throw new Exception("Invalid parameter number " + (i + 1));
			}
			args.set(i, converted);
		}

		final Object[] argsToUse = args.toArray(new Object[args.size()]);

		Object v = method.invoke(this, argsToUse);

		if (methodNames[methodId].startsWith("set")) {
			if (bridge.get() != null) {
				bridge.get().markChanged(this, (Integer) v);
				return new Object[] {};
			}
		}

		return new Object[] { TypeConversionRegistry.toLua(v) };
	}

	public void delete() {
		deleted = true;
		if (bridge.get() != null) {
			bridge.get().setDeleted(this);
			bridge.clear();
		}
	}

}

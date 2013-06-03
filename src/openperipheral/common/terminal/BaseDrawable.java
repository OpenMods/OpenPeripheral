package openperipheral.common.terminal;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import openperipheral.TypeConversionRegistry;
import openperipheral.common.tileentity.TileEntityGlassesBridge;
import openperipheral.util.ReflectionHelper;

public abstract class BaseDrawable implements IDrawable {

	protected String[] methodNames;
	
	private boolean deleted = false;

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
	public Object[] callMethod(int methodId, Object[] arguments)
			throws Exception {
		
		if (deleted) {
			return null;
		}
		
		Method method = ReflectionHelper.getMethod(
							this.getClass(),
							new String[] {methodNames[methodId]},
							arguments.length);
		

		ArrayList<Object> args = new ArrayList(Arrays.asList(arguments));
		
		if (method == null) {
			throw new Exception("Invalid number of arguments");
		}
		
		Class[] requiredParameters = method.getParameterTypes();
		
		for (int i = 0; i < requiredParameters.length; i++) {
			Object converted = TypeConversionRegistry.fromLua(args.get(i), requiredParameters[i]);
			if (converted == null) {
				throw new Exception("Invalid parameter number " + (i+1));
			}
			args.set(i, converted);
		}

		final Object[] argsToUse = args.toArray(new Object[args.size()]);
		
		
		Object oldValue = null;
		if  (methodNames[methodId].startsWith("set")) {
			Method getter = ReflectionHelper.getMethod(
								this.getClass(),
								new String[] {"get"+methodNames[methodId].substring(3)},
								0);
			oldValue = getter.invoke(this);
		}
		
		Object newValue = method.invoke(this, argsToUse);
		
		if (oldValue != newValue) {
			if (bridge.get() != null) {
				bridge.get().markChanged(this);
			}
		}
		
		return new Object[] { newValue };
	}
	
	public void delete() {
		deleted = true;
		if (bridge.get() != null) {
			bridge.get().setDeleted(this);
			bridge.clear();
		}
	}
}

package openperipheral.interfaces.oc.wrappers;

import java.util.Map;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.prefab.AbstractValue;
import openperipheral.adapter.DefaultArgNames;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.interfaces.oc.Registries;

import com.google.common.base.Preconditions;

public class ManagedPeripheralWrapper {

	private static class ObjectWrap extends AbstractValue implements ManagedPeripheral {

		private final Object target;

		private final Map<String, IMethodExecutor> methods;

		private final String[] methodsNames;

		public ObjectWrap(Object target, Map<String, IMethodExecutor> methods) {
			this.target = target;
			this.methods = methods;
			this.methodsNames = methods.keySet().toArray(new String[methods.size()]);
		}

		@Override
		public String[] methods() {
			return methodsNames;
		}

		@Override
		public Object[] invoke(String method, Context context, Arguments args) throws Exception {
			IMethodExecutor executor = methods.get(method);
			Preconditions.checkArgument(executor != null, "Invalid method name: '%s'", method);

			Object[] objArgs = args.toArray();
			return executor.startCall(target).setOptionalArg(DefaultArgNames.ARG_CONTEXT, context).call(objArgs);
		}

	}

	public static ManagedPeripheral wrap(Object target) {
		Preconditions.checkNotNull(target, "Can't wrap null");
		Map<String, IMethodExecutor> methods = Registries.OBJECT_METHODS_FACTORY.getAdaptedClass(target.getClass());
		return methods.isEmpty()? null : new ObjectWrap(target, methods);
	}

}

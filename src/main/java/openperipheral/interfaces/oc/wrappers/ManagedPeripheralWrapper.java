package openperipheral.interfaces.oc.wrappers;

import java.util.Map;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.prefab.AbstractValue;
import openperipheral.adapter.DefaultEnvArgs;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.api.Architectures;
import openperipheral.interfaces.oc.ModuleOpenComputers;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;

public class ManagedPeripheralWrapper {

	public static class ObjectWrap extends AbstractValue implements ManagedPeripheral {

		private final Object target;

		private final Map<String, IMethodExecutor> methods;

		private final String[] methodsNames;

		public ObjectWrap(Object target, Map<String, IMethodExecutor> methods) {
			this.target = target;
			this.methods = methods;
			this.methodsNames = methods.keySet().toArray(new String[methods.size()]);
		}

		public ObjectWrap() {
			// stupid persistence...
			this.target = null;
			this.methods = null;
			this.methodsNames = null;
		}

		@Override
		public String[] methods() {
			return methodsNames != null? methodsNames : ArrayUtils.EMPTY_STRING_ARRAY;
		}

		@Override
		public Object[] invoke(String method, Context context, Arguments args) throws Exception {
			Preconditions.checkArgument(method != null && target != null, "This object is no longer valid");

			IMethodExecutor executor = methods.get(method);
			Preconditions.checkArgument(executor != null, "Invalid method name: '%s'", method);

			Object[] objArgs = args.toArray();
			return DefaultEnvArgs.addCommonArgs(executor.startCall(target), Architectures.OPEN_COMPUTERS)
					.setOptionalArg(DefaultEnvArgs.ARG_CONTEXT, context)
					.call(objArgs);
		}

	}

	public static ManagedPeripheral wrap(Object target) {
		Preconditions.checkNotNull(target, "Can't wrap null");
		Map<String, IMethodExecutor> methods = ModuleOpenComputers.OBJECT_METHODS_FACTORY.getAdaptedClass(target.getClass());
		return methods.isEmpty()? null : new ObjectWrap(target, methods);
	}

}

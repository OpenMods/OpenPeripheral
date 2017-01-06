package openperipheral.interfaces.oc;

import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Node;
import openperipheral.adapter.IMethodCall;
import openperipheral.api.Constants;
import openperipheral.api.architecture.IArchitecture;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.converter.IConverter;
import openperipheral.api.helpers.Index;

public class OpenComputersEnv {

	private static class OCArchitecture implements IArchitecture {

		private final IConverter converter;

		public OCArchitecture(IConverter converter) {
			this.converter = converter;
		}

		@Override
		public String architecture() {
			return Constants.ARCH_OPEN_COMPUTERS;
		}

		@Override
		public Object wrapObject(Object target) {
			return ModuleOpenComputers.wrapObject(target);
		}

		@Override
		public Index createIndex(int value) {
			return Index.fromJava(value, 1);
		}

		@Override
		public IConverter getConverter() {
			return converter;
		}
	}

	private static class OCArchitectureAccess extends OCArchitecture implements IArchitectureAccess {
		private final Node ownNode;
		private final Context context;

		private OCArchitectureAccess(Node ownNode, Context context, IConverter converter) {
			super(converter);
			this.ownNode = ownNode;
			this.context = context;
		}

		@Override
		public String callerName() {
			return context.node().address();
		}

		@Override
		public String peripheralName() {
			return ownNode.address();
		}

		@Override
		public boolean canSignal() {
			return context.isRunning() || context.isPaused();
		}

		@Override
		public boolean signal(String name, Object... args) {
			return context.signal(name, args);
		}
	}

	private final IConverter converter;

	public OpenComputersEnv(IConverter converter) {
		this.converter = converter;
	}

	public IArchitectureAccess createAccess(Node ownNode, Context context) {
		return new OCArchitectureAccess(ownNode, context, converter);
	}

	private IMethodCall addCommonArgs(IMethodCall call, Context context) {
		return call
				.setEnv(IConverter.class, converter)
				.setEnv(Context.class, context);
	}

	public IMethodCall addObjectArgs(IMethodCall call, Context context) {
		return addCommonArgs(call, context)
				.setEnv(IArchitecture.class, new OCArchitecture(converter));
	}

	public IMethodCall addPeripheralArgs(IMethodCall call, Node node, Context context) {
		final OCArchitectureAccess wrapper = new OCArchitectureAccess(node, context, converter);
		return addCommonArgs(call, context)
				.setEnv(IArchitecture.class, wrapper)
				.setEnv(IArchitectureAccess.class, wrapper)
				.setEnv(Node.class, node);
	}
}

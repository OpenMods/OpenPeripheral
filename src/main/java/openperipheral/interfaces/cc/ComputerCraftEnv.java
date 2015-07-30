package openperipheral.interfaces.cc;

import openmods.Log;
import openperipheral.adapter.IMethodCall;
import openperipheral.api.Constants;
import openperipheral.api.architecture.IArchitecture;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.converter.IConverter;
import openperipheral.api.helpers.Index;
import openperipheral.interfaces.cc.wrappers.LuaObjectWrapper;

import org.apache.logging.log4j.Level;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class ComputerCraftEnv {

	private static class CCArchitecture implements IArchitecture {
		private final IConverter converter;

		public CCArchitecture(IConverter converter) {
			this.converter = converter;
		}

		@Override
		public String architecture() {
			return Constants.ARCH_COMPUTER_CRAFT;
		}

		@Override
		public Object wrapObject(Object target) {
			return LuaObjectWrapper.wrap(target);
		}

		@Override
		public Index createIndex(int value) {
			return new Index(value + 1, 1);
		}

		@Override
		public IConverter getConverter() {
			return converter;
		}
	}

	private static class CCArchitectureAccess extends CCArchitecture implements IArchitectureAccess {
		private final IComputerAccess access;

		public CCArchitectureAccess(IComputerAccess access, IConverter converter) {
			super(converter);
			this.access = access;
		}

		@Override
		public String callerName() {
			return Integer.toString(access.getID());
		}

		@Override
		public String peripheralName() {
			return access.getAttachmentName();
		}

		@Override
		public boolean canSignal() {
			try {
				// this should throw if peripheral isn't attached
				access.getAttachmentName();
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		public boolean signal(String name, Object... args) {
			try {
				access.queueEvent(name, args);
				return true;
			} catch (Exception e) {
				Log.log(Level.DEBUG, e, "Failed to send signal: %s", name);
			}
			return false;
		}
	}

	private final IConverter converter;

	public ComputerCraftEnv(IConverter converter) {
		this.converter = converter;
	}

	public IArchitectureAccess createAccess(final IComputerAccess access) {
		return new CCArchitectureAccess(access, converter);
	}

	private IMethodCall addCommonArgs(IMethodCall call, ILuaContext context) {
		return call
				.setEnv(Constants.ARG_CONVERTER, converter)
				.setEnv(Constants.ARG_CONTEXT, context);
	}

	public IMethodCall addObjectArgs(IMethodCall call, ILuaContext context) {
		return addCommonArgs(call, context)
				.setEnv(Constants.ARG_ARCHITECTURE, new CCArchitecture(converter));
	}

	public IMethodCall addPeripheralArgs(IMethodCall call, IComputerAccess access, ILuaContext context) {
		final CCArchitectureAccess wrapper = new CCArchitectureAccess(access, converter);
		return addCommonArgs(call, context)
				.setEnv(Constants.ARG_ARCHITECTURE, wrapper)
				.setEnv(Constants.ARG_ACCESS, wrapper)
				.setEnv(Constants.ARG_COMPUTER, access);
	}
}

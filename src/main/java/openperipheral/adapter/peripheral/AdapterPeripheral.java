package openperipheral.adapter.peripheral;

import java.util.Arrays;

import openmods.Log;
import openperipheral.adapter.AdapterLogicException;
import openperipheral.adapter.composed.ClassMethodsList;
import openperipheral.api.IAttachable;
import openperipheral.util.PeripheralUtils;
import openperipheral.util.ResourceMount;

import org.apache.logging.log4j.Level;

import com.google.common.base.Preconditions;

import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class AdapterPeripheral implements IPeripheral {

	private static final String MOUNT_NAME = "openp";
	private static final IMount MOUNT = new ResourceMount();

	protected final String type;
	protected final Object targetObject;
	protected final ClassMethodsList<IPeripheralMethodExecutor> wrapped;

	public AdapterPeripheral(ClassMethodsList<IPeripheralMethodExecutor> wrapper, Object targetObject) {
		this.targetObject = targetObject;
		this.type = PeripheralUtils.getNameForTarget(targetObject);
		this.wrapped = wrapper;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String[] getMethodNames() {
		return wrapped.methodNames;
	}

	@Override
	public Object[] callMethod(final IComputerAccess computer, ILuaContext context, int index, Object[] arguments) throws LuaException, InterruptedException {
		// this should throw if peripheral isn't attached
		computer.getAttachmentName();

		IPeripheralMethodExecutor executor = wrapped.getMethod(index);
		Preconditions.checkNotNull(executor, "Invalid method index: %d", index);

		try {
			return executor.execute(computer, context, targetObject, arguments);
		} catch (InterruptedException e) {
			// not our problem
			throw e;
		} catch (LuaException e) {
			throw e;
		} catch (AdapterLogicException e) {
			String methodName = wrapped.methodNames[index];
			Log.log(Level.DEBUG, e.getCause(), "Adapter error during method %s(%d) execution on peripheral %s, args: %s",
					methodName, index, type, Arrays.toString(arguments));
			throw e.rethrow();
		} catch (Throwable e) {
			String methodName = wrapped.methodNames[index];
			Log.log(Level.INFO, e, "Unwrapped error during method %s(%d) execution on peripheral %s, args: %s",
					methodName, index, type, Arrays.toString(arguments));
			throw new LuaException("Internal error. Check logs for info");
		}
	}

	@Override
	public void attach(IComputerAccess computer) {
		computer.mount(MOUNT_NAME, AdapterPeripheral.MOUNT);
		if (targetObject instanceof IAttachable) ((IAttachable)targetObject).addComputer(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		if (targetObject instanceof IAttachable) ((IAttachable)targetObject).removeComputer(computer);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return other == this;
	}
}

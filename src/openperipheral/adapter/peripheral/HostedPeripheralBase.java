package openperipheral.adapter.peripheral;

import java.util.Arrays;
import java.util.logging.Level;

import net.minecraft.nbt.NBTTagCompound;
import openmods.Log;
import openperipheral.adapter.WrappedException;
import openperipheral.adapter.composed.ClassMethodsList;
import openperipheral.api.cc15x.IAttachable;
import openperipheral.util.PeripheralUtils;
import openperipheral.util.ResourceMount;

import com.google.common.base.Preconditions;

import dan200.computer.api.*;

public class HostedPeripheralBase<T> implements IHostedPeripheral {

	private static final String MOUNT_NAME = "openp";
	private static final IMount MOUNT = new ResourceMount();

	protected final String type;
	protected final T targetObject;
	protected final ClassMethodsList<IPeripheralMethodExecutor> wrapped;

	protected HostedPeripheralBase(ClassMethodsList<IPeripheralMethodExecutor> wrapper, T targetObject) {
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
	public Object[] callMethod(final IComputerAccess computer, ILuaContext context, int index, Object[] arguments) throws Exception {
		// this should throw if peripheral isn't attached
		computer.getAttachmentName();

		IPeripheralMethodExecutor executor = wrapped.getMethod(index);
		Preconditions.checkArgument(executor != null, "Invalid method index: %d", index);

		try {
			return executor.execute(computer, context, targetObject, arguments);
		} catch (InterruptedException e) {
			// not our problem
			throw e;
		} catch (WrappedException e) {
			String methodName = wrapped.methodNames[index];
			Log.log(Level.FINE, e.getCause(), "Adapter error during method %s(%d) execution on peripheral %s, args: %s",
					methodName, index, type, Arrays.toString(arguments));
			throw e;
		} catch (Exception e) {
			String methodName = wrapped.methodNames[index];
			Log.log(Level.FINE, e, "Internal error during method %s(%d) execution on peripheral %s, args: %s",
					methodName, index, type, Arrays.toString(arguments));
			// don't wrap, Exception has special meaning (reset)
			throw e;
		}
	}

	@Override
	public boolean canAttachToSide(int side) {
		return true;
	}

	@Override
	public void attach(IComputerAccess computer) {
		computer.mount(MOUNT_NAME, HostedPeripheralBase.MOUNT);
		if (targetObject instanceof IAttachable) ((IAttachable)targetObject).addComputer(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		if (targetObject instanceof IAttachable) ((IAttachable)targetObject).removeComputer(computer);
	}

	@Override
	public void update() {}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {}
}

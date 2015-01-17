package openperipheral.interfaces.cc.wrappers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import openmods.Log;
import openperipheral.adapter.AdapterLogicException;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.WrappedEntityBase;
import openperipheral.api.IAttachable;
import openperipheral.api.IWorldProvider;
import openperipheral.interfaces.cc.executors.*;
import openperipheral.interfaces.cc.executors.SynchronousExecutor.TileEntityExecutor;
import openperipheral.interfaces.cc.executors.SynchronousExecutor.WorldProviderExecutor;
import openperipheral.util.PeripheralUtils;
import openperipheral.util.ResourceMount;

import org.apache.logging.log4j.Level;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class AdapterPeripheral extends WrappedEntityBase implements IPeripheral {

	private static final String MOUNT_NAME = "openp";
	private static final IMount MOUNT = new ResourceMount();

	protected final String type;
	protected final Object target;
	private final List<PeripheralExecutor<?>> executors;

	public AdapterPeripheral(Map<String, IMethodExecutor> methods, Object target) {
		super(methods);
		this.type = PeripheralUtils.getNameForTarget(target);
		this.target = target;

		ImmutableList.Builder<PeripheralExecutor<?>> executors = ImmutableList.builder();

		for (IMethodExecutor method : this.methods)
			executors.add(selectExecutor(target, method));

		this.executors = executors.build();
	}

	@SuppressWarnings("unchecked")
	private PeripheralExecutor<Object> getExecutor(int index) {
		return (PeripheralExecutor<Object>)executors.get(index);
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String[] getMethodNames() {
		return super.getMethodNames();
	}

	@Override
	public Object[] callMethod(final IComputerAccess computer, ILuaContext context, int index, Object[] arguments) throws LuaException, InterruptedException {
		// this should throw if peripheral isn't attached
		computer.getAttachmentName();

		IMethodExecutor method = getMethod(index);
		Preconditions.checkNotNull(method, "Invalid method index: %d", index);

		PeripheralExecutor<Object> executor = getExecutor(index);

		try {
			return executor.execute(method, target, computer, context, arguments);
		} catch (InterruptedException e) {
			// not our problem
			throw e;
		} catch (LuaException e) {
			throw e;
		} catch (AdapterLogicException e) {
			String methodName = getMethodName(index);
			Log.log(Level.DEBUG, e.getCause(), "Adapter error during method %s(%d) execution on peripheral %s, args: %s",
					methodName, index, type, Arrays.toString(arguments));
			throw e.rethrow();
		} catch (Throwable e) {
			String methodName = getMethodName(index);
			Log.log(Level.INFO, e, "Unwrapped error during method %s(%d) execution on peripheral %s, args: %s",
					methodName, index, type, Arrays.toString(arguments));
			throw new LuaException("Internal error. Check logs for info");
		}
	}

	@Override
	public void attach(IComputerAccess computer) {
		computer.mount(MOUNT_NAME, AdapterPeripheral.MOUNT);
		if (target instanceof IAttachable) ((IAttachable)target).addComputer(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		if (target instanceof IAttachable) ((IAttachable)target).removeComputer(computer);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return other == this;
	}

	private static PeripheralExecutor<?> selectExecutor(Object target, IMethodExecutor method) {
		if (method.isAsynchronous()) return AsynchronousExecutor.INSTANCE;
		if (target instanceof TileEntity) return TileEntityExecutor.INSTANCE;
		if (target instanceof IWorldProvider) return WorldProviderExecutor.INSTANCE;

		throw new IllegalArgumentException(String.format("Class %s is not valid target for synchronized methods", target.getClass()));
	}
}

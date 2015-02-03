package openperipheral.interfaces.cc.wrappers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import openmods.Log;
import openmods.utils.CachedFactory;
import openperipheral.adapter.AdapterLogicException;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.WrappedEntityBase;
import openperipheral.api.adapter.IWorldProvider;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.architecture.IAttachable;
import openperipheral.api.architecture.cc.IComputerCraftAttachable;
import openperipheral.api.peripheral.IOpenPeripheral;
import openperipheral.interfaces.cc.ComputerCraftEnv;
import openperipheral.interfaces.cc.ResourceMount;
import openperipheral.interfaces.cc.executors.*;
import openperipheral.interfaces.cc.executors.SynchronousExecutor.TileEntityExecutor;
import openperipheral.interfaces.cc.executors.SynchronousExecutor.WorldProviderExecutor;
import openperipheral.util.NameUtils;

import org.apache.logging.log4j.Level;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class AdapterPeripheral extends WrappedEntityBase implements IPeripheral, IOpenPeripheral {

	private static final String MOUNT_NAME = "openp";
	private static final IMount MOUNT = new ResourceMount();

	protected final String type;
	protected final Object target;
	private final List<PeripheralExecutor<?>> executors;

	private final CachedFactory<IComputerAccess, IArchitectureAccess> accessCache = new CachedFactory<IComputerAccess, IArchitectureAccess>() {
		@Override
		protected IArchitectureAccess create(IComputerAccess computer) {
			return ComputerCraftEnv.createAccess(computer);
		}
	};

	public AdapterPeripheral(Map<String, IMethodExecutor> methods, Object target) {
		super(methods);
		this.type = NameUtils.getNameForTarget(target);
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
			throw new LuaException(e.getMessage());
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
		if (target instanceof IAttachable) {
			IArchitectureAccess access = accessCache.getOrCreate(computer);
			((IAttachable)target).addComputer(access);
		}
		if (target instanceof IComputerCraftAttachable) ((IComputerCraftAttachable)target).addComputer(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		if (target instanceof IAttachable) {
			IArchitectureAccess access = accessCache.remove(computer);
			if (access != null) ((IAttachable)target).removeComputer(access);
		}

		if (target instanceof IComputerCraftAttachable) ((IComputerCraftAttachable)target).removeComputer(computer);
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

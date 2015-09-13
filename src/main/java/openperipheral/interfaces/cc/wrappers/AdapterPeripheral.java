package openperipheral.interfaces.cc.wrappers;

import java.util.Arrays;

import openmods.Log;
import openmods.utils.CachedFactory;
import openperipheral.adapter.*;
import openperipheral.adapter.composed.IndexedMethodMap;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.architecture.IAttachable;
import openperipheral.api.architecture.cc.IComputerCraftAttachable;
import openperipheral.api.peripheral.IOpenPeripheral;
import openperipheral.interfaces.cc.*;
import openperipheral.util.DocUtils;

import org.apache.logging.log4j.Level;

import com.google.common.base.Preconditions;

import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class AdapterPeripheral implements IPeripheral, IOpenPeripheral {

	private static final String MOUNT_NAME = "openp";
	private static final IMount MOUNT = new UtilsResourceMount();

	protected final String type;
	protected final Object target;
	private final IMount docMount;

	private final IndexedMethodMap methods;

	private final CachedFactory<IComputerAccess, IArchitectureAccess> accessCache = new CachedFactory<IComputerAccess, IArchitectureAccess>() {
		@Override
		protected IArchitectureAccess create(IComputerAccess computer) {
			return ModuleComputerCraft.ENV.createAccess(computer);
		}
	};

	public AdapterPeripheral(IndexedMethodMap methods, Object target) {
		this.methods = methods;
		this.type = PeripheralTypeProvider.INSTANCE.generateType(target);
		this.target = target;
		this.docMount = new StringMount(DocUtils.createPeripheralHelpText(target.getClass(), type, methods));
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String[] getMethodNames() {
		return methods.getMethodNames();
	}

	private Object[] call(int methodIndex, IMethodExecutor executor, IComputerAccess computer, ILuaContext context, Object[] arguments) throws LuaException, InterruptedException {
		try {
			final IMethodCall call = executor.startCall(target);
			return ModuleComputerCraft.ENV.addPeripheralArgs(call, computer, context).call(arguments);
		} catch (InterruptedException e) {
			throw e;
		} catch (LuaException e) {
			throw e;
		} catch (Throwable e) {
			String methodName = methods.getMethodName(methodIndex);
			Log.log(Level.DEBUG, e, "Error during method %s(%d) execution on peripheral %s, args: %s",
					methodName, methodIndex, type, Arrays.toString(arguments));
			throw new LuaException(AdapterLogicException.getMessageForThrowable(e));
		}
	}

	@Override
	public Object[] callMethod(final IComputerAccess computer, final ILuaContext context, final int index, final Object[] arguments) throws LuaException, InterruptedException {
		// this should throw if peripheral isn't attached
		computer.getAttachmentName();

		final IMethodExecutor method = methods.getMethod(index);
		Preconditions.checkNotNull(method, "Invalid method index: %d", index);

		if (method.isAsynchronous()) return call(index, method, computer, context, arguments);
		else {
			Object[] results = SynchronousExecutor.executeInMainThread(context, new SynchronousExecutor.Task() {
				@Override
				public Object[] execute() throws LuaException, InterruptedException {
					return call(index, method, computer, context, arguments);
				}
			});
			return results;
		}
	}

	@Override
	public void attach(IComputerAccess computer) {
		computer.mount(MOUNT_NAME, AdapterPeripheral.MOUNT);
		computer.mount("rom/help/" + computer.getAttachmentName(), docMount);
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
		if (other == this) return true;
		if (other instanceof AdapterPeripheral) return ((AdapterPeripheral)other).target == target;
		return false;
	}
}

package openperipheral.interfaces.oc.asm.peripheral;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import openmods.Log;
import openperipheral.adapter.IMethodCall;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.wrappers.SignallingGlobals;
import openperipheral.interfaces.oc.asm.ISignallingCallerBase;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

public class TickablePeripheralEnvironmentBase extends PeripheralEnvironmentBase implements ISignallingCallerBase {

	private List<Runnable> tasks = Collections.synchronizedList(Lists.<Runnable> newArrayList());

	public TickablePeripheralEnvironmentBase(Object target) {
		super(target);
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	private static Object[] callForSignal(Object[] args, IMethodCall preparedCall, int callbackId) {
		try {
			Object[] callResult = preparedCall.call(args);
			Object[] fullResult = new Object[callResult.length + 2];
			fullResult[0] = callbackId;
			fullResult[1] = true;
			System.arraycopy(callResult, 0, fullResult, 2, callResult.length);
			return fullResult;
		} catch (Exception e) {
			Log.log(Level.DEBUG, e, "Failed to execute delayed call");
			return new Object[] { callbackId, false, e.getMessage() };
		}
	}

	private static interface ITaskSink {
		public void accept(Runnable task);
	}

	private static final ITaskSink asyncTaskSink = new ITaskSink() {
		@Override
		public void accept(Runnable task) {
			SignallingGlobals.instance.scheduleTask(task);
		}
	};

	private final ITaskSink syncTaskSink = new ITaskSink() {
		@Override
		public void accept(Runnable task) {
			tasks.add(task);
		}
	};

	protected Object[] executeSignallingTask(ITaskSink taskSink, Object target, IMethodExecutor executor, final String signal, final Context context, Arguments arguments) {
		final Object[] args = arguments.toArray();
		final IMethodCall preparedCall = prepareCall(target, executor, context);
		final int callbackId = SignallingGlobals.instance.nextCallbackId();

		taskSink.accept(new Runnable() {
			@Override
			public void run() {
				if (context.isRunning() || context.isPaused()) {
					Object[] result = callForSignal(args, preparedCall, callbackId);
					context.signal(signal, result);
				}
			}
		});

		return new Object[] { callbackId };
	}

	@Override
	public Object[] callSignallingSync(Object target, IMethodExecutor executor, String signal, Context context, Arguments arguments) throws Exception {
		return executeSignallingTask(syncTaskSink, target, executor, signal, context, arguments);
	}

	@Override
	public Object[] callSignallingAsync(Object target, IMethodExecutor executor, String signal, Context context, Arguments arguments) throws Exception {
		return executeSignallingTask(asyncTaskSink, target, executor, signal, context, arguments);
	}

	@Override
	public void update() {
		synchronized (tasks) {
			if (!tasks.isEmpty()) {
				Iterator<Runnable> it = tasks.iterator();
				while (it.hasNext()) {
					it.next().run();
					it.remove();
				}
			}
		}
	}

}

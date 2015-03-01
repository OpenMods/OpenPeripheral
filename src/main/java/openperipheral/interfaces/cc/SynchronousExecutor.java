package openperipheral.interfaces.cc;

import openperipheral.adapter.AdapterLogicException;

import com.google.common.base.Throwables;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaTask;
import dan200.computercraft.api.lua.LuaException;

// CC still needs it, since default .executeInMainThread() incorrectly converts ILuaObjects
public class SynchronousExecutor {
	public static final Object[] DUMMY = new Object[0];

	public interface Task {
		public Object[] execute() throws LuaException, InterruptedException;
	}

	private static class Responder implements ILuaTask {
		private final ILuaContext context;
		private final Task task;
		private boolean nobodyLovesMe;
		public Throwable error;
		public Object[] result;

		public Responder(ILuaContext context, Task task) {
			this.context = context;
			this.task = task;
		}

		public void waitForEvent(long transactionId) throws LuaException, InterruptedException {
			while (!nobodyLovesMe) {
				Object[] result;
				try {
					// internal CC event
					result = context.pullEvent("task_complete");
				} catch (LuaException e) {
					nobodyLovesMe = true;
					throw e;
				} catch (InterruptedException e) {
					nobodyLovesMe = true;
					throw e;
				} catch (Throwable t) {
					nobodyLovesMe = true;
					throw Throwables.propagate(t);
				}

				long receivedTransactionId = ((Number)result[1]).longValue();
				if (transactionId == receivedTransactionId) {
					boolean success = ((Boolean)result[2]);
					if (!success) throw new LuaException(String.valueOf(result[3]));
					break;
				}
			}
		}

		@Override
		public Object[] execute() {
			try {
				result = task.execute();
			} catch (Throwable e) {
				error = e;
			}

			return DUMMY;
		}
	}

	public static Object[] executeInMainThread(ILuaContext context, Task task) throws LuaException, InterruptedException {
		final Responder responder = new Responder(context, task);
		long taskId = context.issueMainThreadTask(responder);

		responder.waitForEvent(taskId);

		// This code was executed in main thread, so there are no special exceptions we need to pass
		final Throwable error = responder.error;
		if (error != null) {
			if (error instanceof LuaException) throw (LuaException)error;
			else throw new LuaException(AdapterLogicException.getMessageForThrowable(error));
		}
		return responder.result;
	}
}

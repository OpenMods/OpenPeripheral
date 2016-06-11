package openperipheral.adapter.wrappers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import openperipheral.Config;

public class SignallingGlobals {

	private static class SimpleThreadFactory implements ThreadFactory {
		private static final String PREFIX = "OP-signalling";
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);

		private SimpleThreadFactory() {
			group = new ThreadGroup(PREFIX);
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, PREFIX + "-" + threadNumber.getAndIncrement());
			if (t.isDaemon()) t.setDaemon(false);
			return t;
		}
	}

	public static final SignallingGlobals instance = new SignallingGlobals();

	private final ExecutorService threadPool = new ThreadPoolExecutor(0,
			Config.signallingPoolSize,
			60L, TimeUnit.SECONDS,
			new SynchronousQueue<Runnable>(),
			new SimpleThreadFactory());

	private final AtomicInteger callbackCounter = new AtomicInteger();

	public int nextCallbackId() {
		return callbackCounter.getAndIncrement();
	}

	public void scheduleTask(Runnable runnable) {
		threadPool.execute(runnable);
	}
}

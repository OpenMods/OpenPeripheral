package openperipheral.interfaces.oc.asm;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Map;
import openperipheral.adapter.IMethodExecutor;

public class MethodsStore {

	private static final Map<Integer, IMethodExecutor[]> DROPBOX = Maps.newHashMap();

	private static int id;

	// Used in ASM, don't rename
	public synchronized static IMethodExecutor[] collect(int id) {
		IMethodExecutor[] prev = DROPBOX.remove(id);
		Preconditions.checkNotNull(prev);
		return prev;
	}

	public synchronized static int drop(IMethodExecutor[] methods) {
		IMethodExecutor[] prev = DROPBOX.put(id, methods);
		Preconditions.checkState(prev == null);
		return id++;
	}

}

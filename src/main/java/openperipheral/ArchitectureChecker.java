package openperipheral;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import openperipheral.api.architecture.IArchitectureChecker;

public class ArchitectureChecker implements IArchitectureChecker {

	public static final ArchitectureChecker INSTANCE = new ArchitectureChecker();

	public interface IArchitecturePredicate {
		public boolean isEnabled();
	}

	private Map<String, IArchitecturePredicate> architectureCheckers = Maps.newHashMap();

	@Override
	public Set<String> knownArchitectures() {
		return Collections.unmodifiableSet(architectureCheckers.keySet());
	}

	@Override
	public boolean isEnabled(String architecture) {
		final IArchitecturePredicate checker = architectureCheckers.get(architecture);
		return checker != null? checker.isEnabled() : false;
	}

	public void register(String architecture, IArchitecturePredicate predicate) {
		final IArchitecturePredicate prev = architectureCheckers.put(architecture, predicate);
		Preconditions.checkState(prev == null, "Duplicate checker for architecture '%s', '%s' -> '%s'", architecture, prev, predicate);
	}
}

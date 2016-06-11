package openperipheral.adapter;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public abstract class RestrictedMethodExecutor implements IMethodExecutor {

	private final Set<String> excludedArchitectures;

	private final Set<String> featureGroups;

	public RestrictedMethodExecutor(Set<String> excludedArchitectures, Set<String> featureGroups) {
		this.excludedArchitectures = ImmutableSet.copyOf(excludedArchitectures);
		this.featureGroups = ImmutableSet.copyOf(featureGroups);
	}

	@Override
	public boolean canInclude(String architecture) {
		if (this.excludedArchitectures.contains(architecture)) return false;

		for (String fg : featureGroups)
			if (!FeatureGroupManager.INSTANCE.isEnabled(fg, architecture)) return false;

		return true;
	}

	@Override
	public Set<String> featureGroups() {
		return featureGroups;
	}

}

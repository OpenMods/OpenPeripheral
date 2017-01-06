package openperipheral.adapter.property;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import openperipheral.adapter.IMethodCall;
import openperipheral.adapter.IMethodDescription;
import openperipheral.adapter.RestrictedMethodExecutor;
import openperipheral.api.converter.IConverter;

public class PropertyExecutor extends RestrictedMethodExecutor {

	public static final Set<Class<?>> NEEDED_ENV = ImmutableSet.<Class<?>> of(IConverter.class);

	private final IMethodDescription description;

	private final IPropertyExecutor caller;

	public PropertyExecutor(IMethodDescription description, IPropertyExecutor caller, Set<String> excludedArchitectures, Set<String> featureGroups) {
		super(excludedArchitectures, featureGroups);
		this.description = description;
		this.caller = caller;
	}

	@Override
	public IMethodDescription description() {
		return description;
	}

	@Override
	public IMethodCall startCall(final Object target) {
		return new IMethodCall() {
			private IConverter converter;

			@Override
			public <T> IMethodCall setEnv(Class<? super T> intf, T instance) {
				if (intf == IConverter.class) this.converter = (IConverter)instance;
				return this;
			}

			@Override
			public Object[] call(Object... args) {
				Preconditions.checkNotNull(converter, "Converter not provided");
				return caller.call(converter, target, args);
			}
		};
	}

	@Override
	public boolean isAsynchronous() {
		return true;
	}

	@Override
	public Optional<String> getReturnSignal() {
		return Optional.absent();
	}

	@Override
	public Set<Class<?>> requiredEnv() {
		return NEEDED_ENV;
	}
}
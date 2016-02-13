package openperipheral.adapter.property;

import java.util.Map;
import java.util.Set;

import openperipheral.adapter.IMethodCall;
import openperipheral.adapter.IMethodDescription;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.api.Constants;
import openperipheral.api.converter.IConverter;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class PropertyExecutor implements IMethodExecutor {

	public static final Map<String, Class<?>> NEEDED_ENV = ImmutableMap.<String, Class<?>> of(Constants.ARG_CONVERTER, IConverter.class);

	private final IMethodDescription description;

	private final IPropertyExecutor caller;

	private final Set<String> excludedArchitectures;

	public PropertyExecutor(IMethodDescription description, IPropertyExecutor caller, Set<String> excludedArchitectures) {
		this.description = description;
		this.caller = caller;
		this.excludedArchitectures = ImmutableSet.copyOf(excludedArchitectures);
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
			public IMethodCall setEnv(String name, Object value) {
				if (Constants.ARG_CONVERTER.equals(name)) this.converter = (IConverter)value;
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
	public boolean canInclude(String architecture) {
		return !this.excludedArchitectures.contains(architecture);
	}

	@Override
	public Map<String, Class<?>> requiredEnv() {
		return NEEDED_ENV;
	}
}
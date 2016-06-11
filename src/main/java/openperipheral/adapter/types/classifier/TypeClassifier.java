package openperipheral.adapter.types.classifier;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.adapter.ITypeClassifier;

public class TypeClassifier implements ITypeClassifier {

	private static class ClassQualifierAdapter implements IGenericClassifier {
		private final IClassClassifier wrapped;

		public ClassQualifierAdapter(IClassClassifier wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public IScriptType classify(ITypeClassifier classifier, Type type) {
			final TypeToken<?> token = TypeToken.of(type);
			return wrapped.classify(classifier, token.getRawType());
		}

	}

	public static final TypeClassifier INSTANCE = new TypeClassifier();

	private final List<IGenericClassifier> classifiers = Lists.newArrayList();

	public TypeClassifier() {
		registerDefaultClassifiers();
	}

	protected void registerDefaultClassifiers() {
		registerClassifier(new DefaultTypeClassifier());
	}

	@Override
	public void registerClassifier(IGenericClassifier classifier) {
		classifiers.add(classifier);
	}

	@Override
	public void registerClassifier(IClassClassifier classifier) {
		classifiers.add(new ClassQualifierAdapter(classifier));
	}

	@Override
	public void registerType(final Class<?> cls, final IScriptType type) {
		final TypeToken<?> match = TypeToken.of(cls);
		classifiers.add(new IGenericClassifier() {
			@Override
			public IScriptType classify(ITypeClassifier classifier, Type t) {
				return (match.isAssignableFrom(t))? type : null;
			}
		});
	}

	@Override
	public IScriptType classifyType(Type type) {
		for (IGenericClassifier c : classifiers) {
			IScriptType result = c.classify(this, type);
			if (result != null) return result;
		}

		throw new IllegalArgumentException(String.format("Can't classify type '%s'", type));
	}
}

package openperipheral.api.helpers;

import com.google.common.reflect.TypeToken;
import openperipheral.api.meta.IMetaProvider;

@SuppressWarnings("serial")
public abstract class MetaProviderSimple<C> implements IMetaProvider<C> {
	private final TypeToken<C> type = new TypeToken<C>(getClass()) {};

	@Override
	@SuppressWarnings("unchecked")
	public final Class<? extends C> getTargetClass() {
		return (Class<? extends C>)type.getRawType();
	}
}

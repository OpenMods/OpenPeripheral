package openperipheral.api.meta;

import java.util.Set;

public interface IPartialMetaBuilder<T> {
	public Set<String> getKeys(T target);
}

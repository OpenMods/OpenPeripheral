package openperipheral.api;

import java.util.Map;

public interface IEntityMetadataProvider<C> extends IMetaProvider<C> {

	public void buildMeta(Map<String, Object> output, C target);

}

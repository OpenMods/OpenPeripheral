package openperipheral.api.meta;

public interface IEntityCustomMetaProvider<C> extends IEntityMetaProvider<C> {

	public boolean canApply(C target);

}

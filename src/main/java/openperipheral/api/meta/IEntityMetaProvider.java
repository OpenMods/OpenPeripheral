package openperipheral.api.meta;

import net.minecraft.util.math.Vec3d;

/**
 * This interface is used to return information about in-game entities. It can be registered in {@link IEntityMetaBuilder#register(IEntityMetaProvider)}.
 * Collected result (from all registered providers) can be created by calling {@link IEntityMetaBuilder#getEntityMetadata(net.minecraft.entity.Entity, Vec3)}.
 */
public interface IEntityMetaProvider<C> extends IMetaProvider<C> {

	public Object getMeta(C target, Vec3d relativePos);

}

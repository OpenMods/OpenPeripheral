package openperipheral.api.meta;

import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public interface IEntityPartialMetaBuilder extends IEntityMetaBuilder, IPartialMetaBuilder<Entity> {

	public Map<String, Object> getBasicEntityMetadata(Entity entity, Vec3d relativePos);

	public Object getEntityMetadata(String key, Entity entity, Vec3d relativePos);

	public IMetaProviderProxy createProxy(Entity entity, Vec3d relativePos);
}

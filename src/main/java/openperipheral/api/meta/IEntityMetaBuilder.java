package openperipheral.api.meta;

import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import openperipheral.api.IApiInterface;

public interface IEntityMetaBuilder extends IApiInterface {
	public Map<String, Object> getEntityMetadata(Entity entity, Vec3d relativePos);

	public void register(IEntityMetaProvider<?> provider);
}

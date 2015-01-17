package openperipheral.api;

import java.lang.annotation.*;

import net.minecraft.world.WorldProvider;

/**
 * Suppresses warning about unavailable world instance (needed for synchronized methods).
 * If target type does not provide World (is not TileEntity or {@link WorldProvider}, method won't be called (exception will be thrown)
 *
 * @deprecated Warning was removed, since it was generally ineffective and confusing
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.PACKAGE })
public @interface Synchronizable {
	public boolean value() default true;
}

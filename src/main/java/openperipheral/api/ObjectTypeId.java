package openperipheral.api;

import java.lang.annotation.*;

/**
 * Used for creating custom names for generated {@link dan200.computercraft.api.lua.ILuaObject} and source id of generated adapters
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ObjectTypeId {
	public String value();
}

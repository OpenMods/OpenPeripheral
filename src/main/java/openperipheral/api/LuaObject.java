package openperipheral.api;

import java.lang.annotation.*;

/**
 * Types marked with this annotation will be converted to callable object (like ILuaObject) when returned to Lua
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LuaObject {}

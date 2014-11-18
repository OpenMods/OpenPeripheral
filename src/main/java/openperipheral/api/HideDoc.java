package openperipheral.api;

import java.lang.annotation.*;

/**
 * Method marked with {@link LuaCallable} but also with this annotion will not be visible in documentation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HideDoc {

}

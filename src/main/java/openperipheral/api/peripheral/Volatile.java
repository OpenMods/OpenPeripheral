package openperipheral.api.peripheral;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Peripheral created for types marked with this annotation will not be cached (i.e. one will be created on any call).
 *
 * @deprecated No longer implemented
 */
@Deprecated
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Volatile {

}

package openperipheral.api.peripheral;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OpenPeripheral will skip peripheral generation for classes marked with this annotations.
 * We will be very sad if you ever use that. We work so hard to make you happy.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Ignore {

}

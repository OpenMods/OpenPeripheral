package openperipheral.api;

import java.lang.annotation.*;

/**
 * Used for creating custom names for source id of inline adapters
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdapterSourceName {
	public String value();
}

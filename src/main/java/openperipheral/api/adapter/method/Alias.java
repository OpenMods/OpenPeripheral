package openperipheral.api.adapter.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows to specify additional script names for method (alongside {@link ScriptCallable#name()}.
 * Those names will be visible as additional entries, but they will all point to same method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Alias {
	public String[] value();
}

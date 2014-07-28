package openperipheral.implementations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiImplementation {
	public boolean includeSuper() default true;

	public boolean cacheable() default true;
}

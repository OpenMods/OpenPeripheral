package openperipheral.api;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProxyArg {
	public String argName() default "proxy";

	public String[] methodNames() default {};

	public Class<?>[] args() default { void.class };
}

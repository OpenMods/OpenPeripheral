package openperipheral.api;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProxyArgs {
	public ProxyArg[] value() default {};
}

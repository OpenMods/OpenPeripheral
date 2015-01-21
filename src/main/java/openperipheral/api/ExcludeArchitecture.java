package openperipheral.api;

import java.lang.annotation.*;

@Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcludeArchitecture {
	public String[] value();
}

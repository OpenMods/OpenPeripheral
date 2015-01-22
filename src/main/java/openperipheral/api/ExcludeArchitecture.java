package openperipheral.api;

import java.lang.annotation.*;

/**
 * Used to mark method that shouldn't be visible in some architectures.
 */
@Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcludeArchitecture {
	/**
	 * Excluded architectures. See {@link Constants}.
	 */
	public String[] value();
}

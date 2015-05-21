package openperipheral.api.struct;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StructField {
	public static final int AUTOASSIGN = -1;

	public int index() default AUTOASSIGN;

	public boolean isOptional() default false;
}

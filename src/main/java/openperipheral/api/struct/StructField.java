package openperipheral.api.struct;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker for fields that have to serialized in classes marked with {@link ScriptStruct}
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StructField {
	public static final int AUTOASSIGN = Integer.MIN_VALUE;

	/**
	 * Index used during conversion from/to table
	 */
	public int index() default AUTOASSIGN;

	/**
	 * Can this field be missing from input map/table
	 */
	public boolean optional() default false;
}

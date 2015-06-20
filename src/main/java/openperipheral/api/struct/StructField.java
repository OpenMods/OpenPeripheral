package openperipheral.api.struct;

import java.lang.annotation.*;

/**
 * Marker for fields that have to serialized in classes marked with {@link ScriptStruct}
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StructField {
	public static final int AUTOASSIGN = -1;

	/**
	 * Index used during conversion from/to table
	 */
	public int index() default AUTOASSIGN;

	/**
	 * Can this field be missing from input map/table
	 */
	public boolean optional() default false;
}

package openperipheral.api.struct;

import java.lang.annotation.*;

/**
 * Marker for types that can be safely converted to map/tables.
 * Included fields must be marked with {@link StructField}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScriptStruct {
	public static enum Output {
		/**
		 * Returned value will be map with named fields
		 */
		OBJECT,
		/**
		 * Returned value will be table/array with indexed fields
		 */
		TABLE;
	}

	/**
	 * Defines representation on script side
	 */
	public Output defaultOutput() default Output.OBJECT;
}

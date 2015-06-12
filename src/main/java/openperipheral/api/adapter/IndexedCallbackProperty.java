package openperipheral.api.adapter;

import java.lang.annotation.*;

import openperipheral.api.adapter.method.ArgType;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IndexedCallbackProperty {

	/**
	 * Field name used for naming get/set methods. If empty, original field name will be used.
	 * First letter of name be capitalized, therefore for value {@code XyzZyx} accessors will be named {@code getXyzZyx} and {@code setXyzZyx}
	 */
	public String name() default "";

	/**
	 * Short description of getter
	 */
	public String getterDesc() default "";

	/**
	 * Short description of setter
	 */
	public String setterDesc() default "";

	/**
	 * If true, only getter will be generated
	 */
	public boolean readOnly() default false;

	public boolean nullable() default false;

	public boolean expandable() default false;

	public static final class GetFromFieldType {}

	public Class<?> keyType() default GetFromFieldType.class;

	public ArgType keyDocType() default ArgType.AUTO;

	public Class<?> valueType() default GetFromFieldType.class;

	public ArgType valueDocType() default ArgType.AUTO;
}

package openperipheral.api.adapter;

import java.lang.annotation.*;

import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.struct.ScriptStruct;

/**
 * This annotation is used to mark class fields that should be exposed in Lua as get/set accessors.
 * Such accessors are able to operate on values with complex structure - like arrays, lists, maps, etc.
 *
 * This annotation handles few standard collection interfaces, like lists, maps, arrays, but also types marked with {@link ScriptStruct}.
 * When field has both {@link Property} and this annotation with same name, OpenPeripheral will merge both into single script method.
 * Depending on presence of {@code index} parameter it will either try to set whole structure or just single element.
 *
 * @see Property
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IndexedProperty {

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

	/**
	 * Does setter accept null values?
	 */
	public boolean nullable() default false;

	/**
	 * Will accessing non-existent key expand underlying container with new value
	 */
	public boolean expandable() default true;

	/**
	 * Type of key parameter in script documentation
	 */
	public ArgType keyType() default ArgType.AUTO;

	/**
	 * Type of value parameter in script documentation
	 */
	public ArgType valueType() default ArgType.AUTO;
}

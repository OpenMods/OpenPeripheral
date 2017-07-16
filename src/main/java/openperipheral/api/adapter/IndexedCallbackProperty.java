package openperipheral.api.adapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import openperipheral.api.property.GetTypeFromField;
import openperipheral.api.struct.ScriptStruct;

/**
 * This annotation is used to mark class fields that should be exposed in Lua as get/set accessors.
 * Such accessors are able to operate on values with complex structure - like arrays, lists, maps, etc.
 * Class that uses this annotation must implement {@link IIndexedPropertyCallback}, otherwise wrapping class in peripheral will fail.
 * Every call to accessors will be passed to callback.
 *
 * OpenPeripheral will try to deduce key and value types based on field type.
 * Type deduction supports few standard collection interfaces, like lists, maps, arrays, but also types marked with {@link ScriptStruct}.
 * When field has both {@link Property} and this annotation with same name, OpenPeripheral will merge both into single method.
 * Depending on presence of {@code index} parameter it will either try to set whole structure or just single element.
 *
 * @see IndexedProperty
 */
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

	/**
	 * Does setter accept null values?
	 */
	public boolean nullable() default false;

	/**
	 * Java type of key (used of conversion of {@code index} in {@link IIndexedPropertyCallback#getField(java.lang.reflect.Field, Object)} and {@link IIndexedPropertyCallback#setField(java.lang.reflect.Field, Object, Object)}. May be used if normal deduction fails.
	 */
	public Class<?> keyType() default GetTypeFromField.class;

	/**
	 * Java type of value (used of conversion of {@code value} in {@link IIndexedPropertyCallback#setField(java.lang.reflect.Field, Object, Object)}. May be used if normal deduction fails.
	 */
	public Class<?> valueType() default GetTypeFromField.class;

}

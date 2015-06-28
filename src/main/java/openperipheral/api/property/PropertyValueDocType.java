package openperipheral.api.property;

import java.lang.annotation.*;

import openperipheral.api.adapter.method.ArgType;

/**
 * This annotation is used for declaring custom types in documentation for classes implementing {@link ISingleCustomProperty} and {@link IIndexedCustomProperty}.
 * Without this annotation, normal rules are used, i.e. classification based on template parameters
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyValueDocType {
	public ArgType value();
}

package openperipheral.api.property;

import java.lang.annotation.*;

import openperipheral.api.adapter.method.ArgType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyValueDocType {
	public ArgType value();
}

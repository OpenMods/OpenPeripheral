package openperipheral.api.struct;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScriptStruct {
	public static enum Output {
		OBJECT,
		TABLE;
	}

	public boolean allowTableInput() default true;

	public Output defaultOutput() default Output.OBJECT;
}

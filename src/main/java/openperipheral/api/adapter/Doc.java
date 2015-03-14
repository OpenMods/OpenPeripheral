package openperipheral.api.adapter;

import java.lang.annotation.*;

/**
 * Used to append freeform documentation to peripherals.
 * It will be visible in generated XML documentation and in-game (when possible).
 * For example, for ComputerCraft peripherals will mount files in {@code /rom/help/<side>} with doc text.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Doc {
	public String[] value();
}

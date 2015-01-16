package openperipheral.api;

import java.lang.annotation.*;

/**
 * Used for creating custom names for generated {@link dan200.computercraft.api.peripheral.IPeripheral} and source id of generated adapters
 *
 * @author boq
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PeripheralTypeId {
	public String value();
}

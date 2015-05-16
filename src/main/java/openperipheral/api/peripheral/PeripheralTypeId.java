package openperipheral.api.peripheral;

import java.lang.annotation.*;

import openperipheral.api.Constants;
import openperipheral.api.adapter.AdapterSourceName;

/**
 * Used for creating custom names for peripherals. Useable only on TileEntities. Will also double as @link {@link AdapterSourceName}.
 * For alternative method, see comment for {@link Constants#IMC_NAME_CLASS}
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PeripheralTypeId {
	public String value();
}

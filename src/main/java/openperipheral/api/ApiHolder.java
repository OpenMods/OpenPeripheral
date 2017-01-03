package openperipheral.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import openperipheral.api.adapter.IObjectAdapterRegistry;
import openperipheral.api.adapter.IPeripheralAdapterRegistry;
import openperipheral.api.architecture.cc.IComputerCraftObjectsFactory;
import openperipheral.api.architecture.oc.IOpenComputersObjectsFactory;
import openperipheral.api.converter.IConverterManager;
import openperipheral.api.meta.IEntityMetaBuilder;
import openperipheral.api.meta.IItemStackMetaBuilder;
import openperipheral.api.peripheral.IPeripheralBlacklist;

/**
 * This annotation is used to get implementation of API interfaces (subclasses of {@link IApiInterface}).
 * Static variables marked with this annotation will be filled with instance of requested API (defined by type of variable).
 * Static, single parameter methods will be called with instance of requested API (defined by parameter of type).
 *
 * Most commonly used interfaces:
 * <ul>
 * <li>{@link IComputerCraftObjectsFactory} - for creating ComputerCraft wrappers for normal Java objects</li>
 * <li>{@link IOpenComputersObjectsFactory} - for creating OpenComputers wrappers for normal Java objects</li>
 * <li>{@link IPeripheralAdapterRegistry} - for registering peripheral adapters</li>
 * <li>{@link IObjectAdapterRegistry} - for registering object adapters</li>
 * <li>{@link IEntityMetaBuilder} - for registering metadata providers and getting metadata for in-game entitites</li>
 * <li>{@link IItemStackMetaBuilder} - for registering metadata providers and getting metadata for in-game items</li>
 * <li>{@link IConverterManager} - for getting architecture-specific type converters</li>
 * <li>{@link IPeripheralBlacklist} - for checking if class is blacklisted (i.e. will not generate peripheral)</li>
 * </ul>
 *
 * <strong>Note:</strong> using this annotation will cause class load!
 * If this causes problems, consider switching to {@link ApiAccess#getApi(Class)}.
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiHolder {}

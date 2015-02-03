/**
 * Main purpose of OpenPeripheral is to automatically generate ComputerCraft or OpenComputers peripherals for mods that don't do it for themselves.
 * It may also be used by mod creators who don't want to write their own addons using bare APIs of mentioned mods.
 *
 * <h3>Features</h3>
 * <ul>
 * <li>Automatical generation of peripherals/environments</li>
 * <li>Transparent and extensible conversion of argument and return values from and to Lua</li>
 * <li>Automatic documentation for peripherals with {@code listMethods} and {@code getAdvancedMethodsData}</li>
 * <li>Generated peripherals will contain every method applicable to TileEntity and implemented interfaces</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * OpenPeripheral will automatically provide peripherals for every tile entity that has methods known to mod (i.e. declared in adapters).
 * When providing peripheral for ComputerCraft, it won't operate on TE if it already implements IPeripheral - OpenPeripheral only works for TEs without CC integration defined by author.
 * When providing peripheral for OpenComputers, it will merge own methods will low priority, so it shouldn't interfere with existing drivers
 *
 * OpenPeripheral can be also used to wrap objects to ComputerCraft structures (like {@link dan200.computercraft.api.peripheral.IPeripheral} and {@link dan200.computercraft.api.lua.ILuaObject}). It can be done by using {@link openperipheral.api.architecture.cc.IComputerCraftObjectsFactory}
 *
 * <h3>Adapters</h3>
 * Adapters are source of information about method that can be exposed to Lua user.
 * They usually contain Java methods (see {@link openperipheral.api.adapter.method.LuaCallable} with metadata that will be used for documentation and validation.
 * Adapters declare target class, that is later used for determining, which methods will be used for peripheral
 * Adapter can be defined in two places:
 * <ul>
 * <li>external adapters - declared as stand alone classes (implementing either {@link openperipheral.api.adapter.IPeripheralAdapter} or {@link openperipheral.api.adapter.IObjectAdapter}
 * <li>internal/inline adapters - declared directly in wrapped objects</li>
 * </ul>
 *
 * <h3>Blacklisting tile entities</h3>
 * Due to inner working of ComputerCraft we can check if there are any other peripheral providers.
 * If mod author has chosen to provider integration with providers, OpenPeripheral implementation may sometimes hide actual, non-generated integration.
 * In this cases there are few ways to prevent our handler from creating adapter for TileEntity:
 * <ul>
 * <li>{@link openperipheral.api.peripheral.Ignore} annotation on TileEntity class</li>
 * <li>Any field called {@code OPENPERIPHERAL_IGNORE} in TileEntity class</li>
 * <li>IMC message with id {@code ignoreTileEntity} and full class name as value</li>
 * <li>Explicit registration via {@link openperipheral.api.peripheral.IPeripheralBlacklist#addToBlacklist(Class)}
 * </ul>
 *
 * <h3>Method arguments</h3>
 * Methods declared in adapters must declare arguments in following order:
 * <ol>
 * <li>Java arguments - have name that declares it's purpose (see below)</li>
 * <li>Converted Lua arguments - must be marked with {@link openperipheral.api.adapter.method.Arg} annotation</li>
 * </ol>
 *
 * During call Java arguments marked with {@link openperipheral.api.adapter.method.Env} are filled with values depending on it's names and types. Some of them are predefined in {@link openperipheral.api.Constants}
 */

@API(apiVersion = openperipheral.api.ApiAccess.API_VERSION, owner = "OpenPeripheralCore", provides = "OpenPeripheralApi")
package openperipheral.api;

import cpw.mods.fml.common.API;


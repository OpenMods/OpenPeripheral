/**
 * <p>Properties are fields that are exposed on script side as pair methods: getter and setter.
 * There are currently two types of properties: 'single', which allow accesing value of field directly and 'indexed' which allow manipulation of parts of more complex types (like lists, maps, {@link openperipheral.api.struct.ScriptStruct}, etc.</p>
 *
 * <p>Properties either can have default behaviour ({@link openperipheral.api.adapter.Property} and {@link openperipheral.api.adapter.IndexedProperty} or delegate calls to owner object ({@link openperipheral.api.adapter.CallbackProperty} and {@link openperipheral.api.adapter.IndexedCallbackProperty}</p>
 *
 * <p>Due to legacy reasons, basic interface for this functionality are defined in root package.</p>
 *
 * @see openperipheral.api.adapter.Property
 * @see openperipheral.api.adapter.CallbackProperty
 * @see openperipheral.api.adapter.IndexedProperty
 * @see openperipheral.api.adapter.IndexedCallbackProperty
 */
@API(apiVersion = openperipheral.api.ApiAccess.API_VERSION, owner = "OpenPeripheralCore", provides = "OpenPeripheralApi")
package openperipheral.api.property;

import net.minecraftforge.fml.common.API;


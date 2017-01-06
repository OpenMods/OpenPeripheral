package openperipheral.api.adapter.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import openperipheral.api.architecture.IArchitecture;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.converter.IConverter;

/**
 * <p>
 * Used to mark {@link ScriptCallable} method arguments as receivers of instance of environment object, selected by type of argument.
 * Available types of argument depend on context and used architecture.
 * </p>
 *
 * <p>
 * If argument cannot be provided in given architecture, whole method will be hidden (e.g. requiring IComputerAccess from ComputerCraft will hide method from OpenComputers).
 * </p>
 *
 * <h3>Common types:</h3>
 * <h4>Always available:</h4>
 * <ul>
 * <li>{@link IArchitecture}</li>
 * <li>{@link IConverter}</li>
 * </ul>
 * <h4>Only on peripherals</h4>
 * <ul>
 * <li>{@link IArchitectureAccess}</li>
 * </ul>
 *
 * <h3>ComputerCraft-only types:</h3>
 * <h4>Always available:</h4>
 * <ul>
 * <li>{@link dan200.computercraft.api.lua.ILuaContext}</li>
 * </ul>
 * <h4>Only on peripherals</h4>
 * <ul>
 * <li>{@link dan200.computercraft.api.peripheral.IComputerAccess}</li>
 * </ul>
 *
 * <h3>OpenComputers-only types:</h3>
 * <h4>Always available:</h4>
 * <ul>
 * <li>{@link li.cil.oc.api.machine.Context}</li>
 * </ul>
 * <h4>Only on peripherals</h4>
 * <ul>
 * <li>{@link li.cil.oc.api.network.Node}</li>
 * </ul>
 *
 * @see Arg
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Env {}

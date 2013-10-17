package openperipheral.api;

import java.util.ArrayList;

import openperipheral.core.util.ReflectionHelper;

/**
 * Used by {@link PeripheralHandler} to control whether or not the computer will
 * be able to access the TileEntity as a peripheral. Any classes that are
 * added to this registry a Computer will not be allowed to interface with.
 */
public class BlacklistRegistry {
	private static ArrayList<Class<?>> blacklistedTileEntities = new ArrayList<Class<?>>();
	
	/**
	 * This method registers a Class with OpenPeripheral so that it will not be interfaced with
	 * 
	 * @param targetClass
	 * @return whether or not the class was successfully added to the blacklist
	 */
	public static boolean registerTileEntity(Class<?> targetClass) {
		if (blacklistedTileEntities.contains(targetClass)) { return false; }
		System.out.println(String.format("Adding class %s to the blacklist", targetClass.getName()));
		return blacklistedTileEntities.add(targetClass);
	}
	
	/**
	 * This method registers a Class, via the class name string, with OpenPeripheral so that it will not be interfaced with
	 * 
	 * @param className
	 * @return whether or not the class was successfully added to the blacklist
	 */
	public static boolean registerTileEntity(String className) {
		Class<?> targetClass = ReflectionHelper.getClass(className);
		return registerTileEntity(targetClass);
	}
	
	/**
	 * This method checks whether or not a class has been registered in the blacklist
	 * 
	 * @param checkClass
	 * @return whether or not the class is registered
	 */
	public static boolean contains(Class<?> checkClass) {
		return blacklistedTileEntities.contains(checkClass);
	}
	
	/**
	 * This method checks, via the class name string, whether or not a class has been registered in the blacklist
	 * 
	 * @param checkClass
	 * @return whether or not the class is registered
	 */
	public static boolean contains(String className) {
		Class<?> checkClass = ReflectionHelper.getClass(className);
		return contains(checkClass);
	}
}

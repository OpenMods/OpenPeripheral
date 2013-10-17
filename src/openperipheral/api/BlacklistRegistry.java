package openperipheral.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.tileentity.TileEntity;

import openperipheral.core.util.ReflectionHelper;

/**
 * Used by {@link PeripheralHandler} to control whether or not the computer will
 * be able to access the TileEntity as a peripheral. Any classes that are
 * added to this registry a Computer will not be allowed to interface with.
 */
public class BlacklistRegistry {
	@SuppressWarnings("unchecked")
	// Add any classes here that should never be allowed onto the blacklist as it could be bad
	private static List<Class<?>> cannotBlacklist = Arrays.asList(TileEntity.class, IPeripheralAdapter.class);
	private static ArrayList<Class<?>> blacklistedTileEntities = new ArrayList<Class<?>>();
	
	/**
	 * This method registers a Class with OpenPeripheral so that it will not be interfaced with
	 * 
	 * @param targetClass
	 * @return whether or not the class was successfully added to the blacklist
	 */
	public static boolean registerClass(Class<?> targetClass) {
		if (cannotBlacklist.contains(targetClass)) {
			System.err.println(String.format("Cannot blacklist %s, it would break all the things!", targetClass.getName()));
			return false;
		}
		if (blacklistedTileEntities.contains(targetClass)) {
			System.out.println(String.format("Class %s is already blacklisted", targetClass.getName()));
			return false;
		}
		System.out.println(String.format("Adding class %s to the blacklist", targetClass.getName()));
		return blacklistedTileEntities.add(targetClass);
	}
	
	/**
	 * This method registers a Class, via the class name string, with OpenPeripheral so that it will not be interfaced with
	 * 
	 * @param className
	 * @return whether or not the class was successfully added to the blacklist
	 */
	public static boolean registerClass(String className) {
		Class<?> targetClass = ReflectionHelper.getClass(className);
		return registerClass(targetClass);
	}
	
	/**
	 * This method checks whether or not a class has been registered in the blacklist
	 * 
	 * @param checkClass
	 * @return whether or not the class is registered
	 */
	public static boolean containsClass(Class<?> checkClass) {
		return blacklistedTileEntities.contains(checkClass);
	}
	
	/**
	 * This method checks, via the class name string, whether or not a class has been registered in the blacklist
	 * 
	 * @param checkClass
	 * @return whether or not the class is registered
	 */
	public static boolean containsClass(String className) {
		Class<?> checkClass = ReflectionHelper.getClass(className);
		return containsClass(checkClass);
	}
}

/**
 * This file is part of the public ComputerCraft API - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2013. This API may be redistributed unmodified and in full only.
 * For help using the API, and posting your mods, visit the forums at computercraft.info.
 */

package dan200.computer.api;
import java.lang.reflect.Method;
import net.minecraft.world.World;

/**
 * The static entry point to the ComputerCraft API.
 * Members in this class must be called after mod_ComputerCraft has been initialised,
 * but may be called before it is fully loaded.
 */
public class ComputerCraftAPI 
{	
	/**
	 * TODO: Document me.
	 */
	public static int createUniqueNumberedSaveDir( World world, String parentSubPath )
	{
		findCC();
		if( computerCraft_createUniqueNumberedSaveDir != null )
		{
			try {
				return ((Integer)computerCraft_createUniqueNumberedSaveDir.invoke( null, world, parentSubPath )).intValue();
			} catch (Exception e){
				// It failed
			}
		}
		return -1;
	}
	
	/**
	 * TODO: Document me.
	 */
	public static IWritableMount createSaveDirMount( World world, String subPath, long capacity )
	{
		findCC();
		if( computerCraft_createSaveDirMount != null )
		{
			try {
				return (IWritableMount)computerCraft_createSaveDirMount.invoke( null, world, subPath, capacity );
			} catch (Exception e){
				// It failed
			}
		}
		return null;
	}
	 
	/**
	 * TODO: Document me.
	 */
	public static IMount createResourceMount( Class modClass, String domain, String subPath )
	{
		findCC();
		if( computerCraft_createResourceMount != null )
		{
			try {
				return (IMount)computerCraft_createResourceMount.invoke( null, modClass, domain, subPath );
			} catch (Exception e){
				// It failed
			}
		}
		return null;
	}
	 
	/**
	 * Registers a peripheral handler for a TileEntity that you do not have access to. Only
	 * use this if you want to expose IPeripheral on a TileEntity from another mod. For your own
	 * mod, just implement IPeripheral on the TileEntity directly.
	 * @see IPeripheral
	 * @see IPeripheralHandler
	 */
	public static void registerExternalPeripheral( Class <? extends net.minecraft.tileentity.TileEntity> clazz, IPeripheralHandler handler )
	{
		findCC();
		if (computerCraft_registerExternalPeripheral != null)
		{
			try {
				computerCraft_registerExternalPeripheral.invoke(null, clazz, handler);
			} catch (Exception e){
				// It failed
			}
		}
	}

	// The functions below here are private, and are used to interface with the non-API ComputerCraft classes.
	// Reflection is used here so you can develop your mod in MCP without decompiling ComputerCraft and including
	// it in your solution.
	
	private static void findCC()
	{
		if( !ccSearched ) {
			try {
				computerCraft = Class.forName( "dan200.ComputerCraft" );
				computerCraft_createUniqueNumberedSaveDir = findCCMethod( "createUniqueNumberedSaveDir", new Class[] {
					World.class, String.class
				} );
				computerCraft_createSaveDirMount = findCCMethod( "createSaveDirMount", new Class[] {
					World.class, String.class, Long.TYPE
				} );
				computerCraft_createResourceMount = findCCMethod( "createResourceMount", new Class[] {
					Class.class, String.class, String.class
				} );
				computerCraft_registerExternalPeripheral = findCCMethod( "registerExternalPeripheral", new Class[] { 
					Class.class, IPeripheralHandler.class 
				} );
			} catch( Exception e ) {
				net.minecraft.server.MinecraftServer.getServer().logInfo( "ComputerCraftAPI: ComputerCraft not found." );
			} finally {
				ccSearched = true;
			}
		}
	}

	private static Method findCCMethod( String name, Class[] args )
	{
		try {
			return computerCraft.getMethod( name, args );
		} catch( NoSuchMethodException e ) {
			net.minecraft.server.MinecraftServer.getServer().logInfo( "ComputerCraftAPI: ComputerCraft method " + name + " not found." );
			return null;
		}
	}	
	
	private static boolean ccSearched = false;	
	private static Class computerCraft = null;
	private static Method computerCraft_createUniqueNumberedSaveDir = null;
	private static Method computerCraft_createSaveDirMount = null;
	private static Method computerCraft_createResourceMount = null;
	private static Method computerCraft_registerExternalPeripheral = null;
}

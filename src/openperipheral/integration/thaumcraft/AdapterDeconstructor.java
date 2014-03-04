/**
 * 
 */
package openperipheral.integration.thaumcraft;

import java.lang.reflect.Field;

import com.google.common.base.Preconditions;

import net.minecraft.inventory.IInventory;

import openmods.utils.ReflectionHelper;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.api.Prefixed;
import thaumcraft.api.aspects.Aspect;

/**
 * @author Katrina
 *
 */
@Prefixed("target")
public class AdapterDeconstructor implements IPeripheralAdapter {
	private static final Class<?> TILE_DECONSTRUCTOR = ReflectionHelper.getClass("thaumcraft.common.tiles.TileDeconstructionTable");
	/* (non-Javadoc)
	 * @see openperipheral.api.IPeripheralAdapter#getTargetClass()
	 */
	@Override
	public Class<?> getTargetClass() {
		return TILE_DECONSTRUCTOR;
	}
	
	@LuaMethod(description="Does the Table have an aspect in it",returnType=LuaType.BOOLEAN)
	public boolean hasAspect(Object target) throws Exception
	{
		Field f = ReflectionHelper.getField(TILE_DECONSTRUCTOR, "aspect");
		Object o = f.get(target);

		if (o == null)
			return false;
		return true;
	}
	
	@LuaMethod(returnType=LuaType.BOOLEAN,description="Has the Table any items")
	public boolean hasItem(Object target) throws Exception
	{
		if(target instanceof IInventory)
		{
			IInventory inv=(IInventory)target;
			return inv.getStackInSlot(0)!=null;
		}
		return false;
	}
	
	@LuaMethod(returnType=LuaType.STRING,description="What aspect does the Table contain")
	public String getAspect(Object target) throws Exception
	{
		Field f = ReflectionHelper.getField(TILE_DECONSTRUCTOR, "aspect");
		Object o = f.get(target);

		if (o == null)
			return "";
		Preconditions.checkState(o instanceof Aspect, "Deconstructing table is broken");
		return ((Aspect)o).getTag();
	}

}

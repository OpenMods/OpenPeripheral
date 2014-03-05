/**
 * 
 */
package openperipheral.integration.thaumcraft;

import java.lang.reflect.Field;

import net.minecraft.inventory.IInventory;
import openmods.utils.ReflectionHelper;
import openperipheral.api.*;
import openperipheral.util.FieldAccessHelpers;
import thaumcraft.api.aspects.Aspect;

/**
 * @author Katrina
 * 
 */
@Prefixed("target")
public class AdapterDeconstructor implements IPeripheralAdapter {
	private static final Class<?> TILE_DECONSTRUCTOR = ReflectionHelper.getClass("thaumcraft.common.tiles.TileDeconstructionTable");

	@Override
	public Class<?> getTargetClass() {
		return TILE_DECONSTRUCTOR;
	}

	@LuaMethod(description = "Does the Table have an aspect in it", returnType = LuaType.BOOLEAN)
	public boolean hasAspect(Object target) throws Exception {
		Field f = ReflectionHelper.getField(TILE_DECONSTRUCTOR, "aspect");
		return f.get(target) != null;
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Has the Table any items")
	public boolean hasItem(Object target) {
		if (target instanceof IInventory)
		{
			IInventory inv = (IInventory)target;
			return inv.getStackInSlot(0) != null;
		}
		return false;
	}

	@LuaMethod(returnType = LuaType.STRING, description = "What aspect does the Table contain")
	public String getAspect(Object target) throws Exception {
		Aspect aspect = FieldAccessHelpers.getField(TILE_DECONSTRUCTOR, target, "aspect", null);
		return aspect != null? aspect.getTag() : "";
	}

}

/**
 * 
 */
package openperipheral.integration.thaumcraft;

import java.lang.reflect.Field;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openmods.utils.ReflectionHelper;
import openperipheral.api.*;

import com.google.common.base.Preconditions;

/**
 * @author Katrina
 * 
 */
@Prefixed("target")
public class AdapterArcaneBore implements IPeripheralAdapter {
	private static final Class<?> TILE_ARCANE_BORE = ReflectionHelper.getClass("thaumcraft.common.tiles.TileArcaneBore");
	private static final Class<?> ITEM_ELEMENTAL_PICK = ReflectionHelper.getClass("thaumcraft.common.items.equipment.ItemElementalPickaxe");

	/*
	 * (non-Javadoc)
	 * 
	 * @see openperipheral.api.IPeripheralAdapter#getTargetClass()
	 */
	@Override
	public Class<?> getTargetClass() {
		return TILE_ARCANE_BORE;
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Does the arcane bore have a pickaxe.")
	public boolean hasPickaxe(Object target) throws Exception
	{
		return getBooleanMethod(target, "hasPickaxe");
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Does the arcane bore have a focus.")
	public boolean hasFocus(Object target) throws Exception
	{
		return getBooleanMethod(target, "hasFocus");
	}

	public ItemStack getPick(Object bore)
	{
		if (bore instanceof IInventory) { return ((IInventory)bore).getStackInSlot(1); }
		return null;
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "is the pick broken?")
	public boolean isPickaxeBroken(Object target) throws Exception
	{
		ItemStack pick = getPick(target);
		return pick != null && pick.getItemDamage() + 1 == pick.getMaxDamage();
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Is the Arcane bore active?")
	public boolean isWorking(Object target) throws Exception
	{
		ItemStack pick = getPick(target);
		return ((Boolean)ReflectionHelper.call(target, "gettingPower", new Object[] {})) && hasFocus(target) && hasPickaxe(target) && pick.isItemStackDamageable() && !isPickaxeBroken(target);
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "gets the radius of the bore's effects")
	public int getRadius(Object target) throws Exception
	{
		return 1 + (getIntMethod(target, "area") + getIntMethod(target, "maxRadius")) * 2;
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "gets the speed of the bore")
	public int getSpeed(Object target) throws Exception
	{
		return getIntMethod(target, "speed");
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Does the bore mine native clusters as well as normal ores")
	public boolean hasNativeClusters(Object target) throws Exception
	{
		ItemStack pick = getPick(target);
		return pick != null && ITEM_ELEMENTAL_PICK.isInstance(pick.getItem());
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Gets the fortune level the Bore is mining with")
	public int getFortune(Object target)
	{
		ItemStack pick = getPick(target);
		return EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, pick);
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Does the Bore mine with silk touch")
	public boolean hasSilkTouch(Object target)
	{
		ItemStack pick = getPick(target);
		return EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, pick) > 0;
	}

	private int getIntMethod(Object target, String method) throws Exception
	{
		Field f = ReflectionHelper.getField(TILE_ARCANE_BORE, method);
		Object o = f.get(target);

		if (o == null) return -1;
		Preconditions.checkState(o instanceof Integer, "Arcane bore is broken");
		return (Integer)o;
	}

	private boolean getBooleanMethod(Object target, String method)
			throws IllegalAccessException {
		Field f = ReflectionHelper.getField(TILE_ARCANE_BORE, method);
		Object o = f.get(target);

		if (o == null) return false;
		Preconditions.checkState(o instanceof Boolean, "Arcane bore is broken");
		return (Boolean)o;
	}

}

/**
 * 
 */
package openperipheral.integration.thaumcraft;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openmods.utils.ReflectionHelper;
import openperipheral.api.*;
import openperipheral.util.FieldAccessHelpers;

/**
 * @author Katrina
 * 
 */
@Prefixed("target")
public class AdapterArcaneBore implements IPeripheralAdapter {

	private static final Class<?> TILE_ARCANE_BORE = ReflectionHelper.getClass("thaumcraft.common.tiles.TileArcaneBore");
	private static final Class<?> ITEM_ELEMENTAL_PICK = ReflectionHelper.getClass("thaumcraft.common.items.equipment.ItemElementalPickaxe");

	@Override
	public Class<?> getTargetClass() {
		return TILE_ARCANE_BORE;
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Does the arcane bore have a pickaxe.")
	public boolean hasPickaxe(Object target) {
		return getBooleanField(target, "hasPickaxe");
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Does the arcane bore have a focus.")
	public boolean hasFocus(Object target) {
		return getBooleanField(target, "hasFocus");
	}

	public ItemStack getPick(Object bore) {
		return (bore instanceof IInventory)? ((IInventory)bore).getStackInSlot(1) : null;
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "is the pick broken?")
	public boolean isPickaxeBroken(Object target) {
		ItemStack pick = getPick(target);
		return pick != null && pick.getItemDamage() + 1 == pick.getMaxDamage();
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Is the Arcane bore active?")
	public boolean isWorking(Object target) {
		ItemStack pick = getPick(target);
		Boolean hasPower = ReflectionHelper.call(target, "gettingPower");
		return hasPower && hasFocus(target) && hasPickaxe(target) && pick.isItemStackDamageable() && !isPickaxeBroken(target);
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "gets the radius of the bore's effects")
	public int getRadius(Object target) {
		return 1 + (getIntField(target, "area") + getIntField(target, "maxRadius")) * 2;
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "gets the speed of the bore")
	public int getSpeed(Object target) {
		return getIntField(target, "speed");
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Does the bore mine native clusters as well as normal ores")
	public boolean hasNativeClusters(Object target) {
		ItemStack pick = getPick(target);
		return pick != null && ITEM_ELEMENTAL_PICK.isInstance(pick.getItem());
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Gets the fortune level the Bore is mining with")
	public int getFortune(Object target) {
		ItemStack pick = getPick(target);
		return EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, pick);
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Does the Bore mine with silk touch")
	public boolean hasSilkTouch(Object target) {
		ItemStack pick = getPick(target);
		return EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, pick) > 0;
	}

	private static int getIntField(Object target, String field) {
		return FieldAccessHelpers.getIntField(TILE_ARCANE_BORE, target, field);
	}

	private static boolean getBooleanField(Object target, String field) {
		return FieldAccessHelpers.getBooleanField(TILE_ARCANE_BORE, target, field);
	}

}

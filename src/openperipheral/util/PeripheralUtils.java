package openperipheral.util;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import openmods.Log;

import com.google.common.base.Strings;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class PeripheralUtils {

	private static Map<String, Class<? extends TileEntity>> teNameToClass;
	private static Map<Class<? extends TileEntity>, String> teClassToName;

	public static Map<Class<? extends TileEntity>, String> getClassToNameMap() {
		if (teClassToName == null) teClassToName = ReflectionHelper.getPrivateValue(TileEntity.class, null, "classToNameMap", "field_70323_b");
		return teClassToName;
	}

	public static Map<String, Class<? extends TileEntity>> getNameToClassMap() {
		if (teNameToClass == null) teNameToClass = ReflectionHelper.getPrivateValue(TileEntity.class, null, "nameToClassMap", "field_70326_a");
		return teNameToClass;
	}

	public static String getNameForTarget(Object target) {
		final String name = tryGetName(target);
		return Strings.isNullOrEmpty(name)? "peripheral" : name.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
	}

	private static String tryGetName(Object target) {
		if (target == null) return "";

		if (target instanceof IInventory) {
			try {
				return ((IInventory)target).getInvName();
			} catch (Throwable t) {
				Log.warn(t, "Can't get inventory name for %s", target.getClass());
			}
		}

		if (target instanceof TileEntity) {
			TileEntity te = (TileEntity)target;
			String name = getClassToNameMap().get(te.getClass());
			if (!Strings.isNullOrEmpty(name)) return name;

			try {
				Block block = te.getBlockType();
				if (block != null) {
					int dmg = te.getBlockMetadata();

					ItemStack is = new ItemStack(block, 1, dmg);
					try {
						return is.getDisplayName();
					} catch (Throwable t) {
						Log.warn(t, "Can't get display name for %s", target.getClass());
					}

					try {
						return is.getUnlocalizedName();
					} catch (Throwable t) {
						Log.warn(t, "Can't get unlocalized name for %s", target.getClass());
					}
				}
			} catch (Throwable t) {
				Log.warn(t, "Exception while getting name from item for %s", target.getClass());
			}

			if (Strings.isNullOrEmpty(name)) name = te.getClass().getSimpleName();
		}

		return "";
	}
}

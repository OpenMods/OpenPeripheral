package openperipheral.util;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import openmods.Log;
import openperipheral.api.PeripheralTypeId;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class NameUtils {

	private static Map<String, Class<? extends TileEntity>> teNameToClass;
	private static Map<Class<? extends TileEntity>, String> teClassToName;

	public static Map<Class<? extends TileEntity>, String> getClassToNameMap() {
		if (teClassToName == null) teClassToName = ReflectionHelper.getPrivateValue(TileEntity.class, null, "classToNameMap", "field_145853_j");
		return teClassToName;
	}

	public static Map<String, Class<? extends TileEntity>> getNameToClassMap() {
		if (teNameToClass == null) teNameToClass = ReflectionHelper.getPrivateValue(TileEntity.class, null, "nameToClassMap", "field_145855_i");
		return teNameToClass;
	}

	public static String getNameForTarget(Object target) {
		final String name = tryGetName(target);
		return Strings.isNullOrEmpty(name)? "peripheral" : name.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
	}

	private static String tryGetName(Object target) {
		if (target == null) return "invalid";

		final Class<? extends Object> cls = target.getClass();

		PeripheralTypeId customId = cls.getAnnotation(PeripheralTypeId.class);
		if (customId != null) return customId.value();

		if (target instanceof IInventory) {
			try {
				return ((IInventory)target).getInventoryName();
			} catch (Throwable t) {
				Log.warn(t, "Can't get inventory name for %s", cls);
			}
		}

		if (target instanceof TileEntity) {
			TileEntity te = (TileEntity)target;

			try {
				String mapping = getClassToNameMap().get(cls);
				if (!Strings.isNullOrEmpty(mapping)) return mapping;
			} catch (Throwable t) {
				Log.warn(t, "Failed to map class %s to name", cls);
			}

			try {
				Block block = te.getBlockType();
				if (block != null) {
					int dmg = te.getBlockMetadata();

					ItemStack is = new ItemStack(block, 1, dmg);
					try {
						String name = is.getDisplayName();
						if (!Strings.isNullOrEmpty(name)) return name;
					} catch (Throwable t) {
						Log.warn(t, "Can't get display name for %s", cls);
					}

					try {
						String name = StringUtils.removeStart(block.getUnlocalizedName(), "tile.");
						if (!Strings.isNullOrEmpty(name)) return name;
					} catch (Throwable t) {
						Log.warn(t, "Can't get unlocalized name for %s", cls);
					}

				}
			} catch (Throwable t) {
				Log.warn(t, "Exception while getting name from item for %s", cls);
			}
		}

		return cls.getSimpleName();
	}

	public static String grumize(Class<?> targetCls) {
		return targetCls.getName().replace('.', '\u2603');
	}
}

package openperipheral.util;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

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
		String name = "";
		if (target instanceof IInventory) {
			name = ((IInventory)target).getInvName();
		} else if (target instanceof TileEntity) {
			TileEntity te = (TileEntity)target;
			name = getClassToNameMap().get(te.getClass());
			if (Strings.isNullOrEmpty(name)) {
				Block block = te.getBlockType();
				int dmg = te.getBlockMetadata();

				ItemStack is = new ItemStack(block, 1, dmg);
				try {
					name = is.getDisplayName();
				} catch (Exception e) {
					try {
						name = is.getUnlocalizedName();
					} catch (Exception e2) {}
				}

				if (Strings.isNullOrEmpty(name)) name = te.getClass().getSimpleName();
			}
		}

		return Strings.isNullOrEmpty(name)? "peripheral" : name.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
	}
}

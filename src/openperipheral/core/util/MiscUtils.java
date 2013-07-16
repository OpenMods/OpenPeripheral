package openperipheral.core.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import openperipheral.OpenPeripheral;
import openperipheral.api.Arg;
import openperipheral.core.MethodDeclaration;

public class MiscUtils {
	public static boolean canBeGlasses(ItemStack stack) {
		// || (Loader.isModLoaded(Mods.MPS) && MPSUtils.isValidHelmet(stack)))
		return stack != null
				&& (stack.getItem() == OpenPeripheral.Items.glasses);
	}

	public static int getHoliday() {
		Calendar today = Calendar.getInstance();
		int month = today.get(2);
		int day = today.get(5);
		if ((month == 1) && (day == 14)) {
			return 1;
		}
		if ((month == 9) && (day == 31)) {
			return 2;
		}
		if ((month == 11) && (day >= 24) && (day <= 30)) {
			return 3;
		}
		return 0;
	}

	public static <T> T[] append(T[] arr, T element) {
		final int N = arr.length;
		arr = Arrays.copyOf(arr, N + 1);
		arr[N] = element;
		return arr;
	}

	public static String getNameForTarget(Object target) {
		String name = "";
		if (target instanceof IInventory) {
			name = ((IInventory) target).getInvName();
		} else if (target instanceof TileEntity) {
			TileEntity te = (TileEntity) target;
			try {
				NBTTagCompound tag = new NBTTagCompound();
				te.writeToNBT(tag);
				name = tag.getString("id");
			} catch (Exception e) {

			}
			if (name == null || name.equals("")) {
				int x = te.xCoord;
				int y = te.yCoord;
				int z = te.zCoord;
				int blockId = te.worldObj.getBlockId(x, y, z);
				Block block = Block.blocksList[blockId];
				int dmg = block.getDamageValue(te.worldObj, x, y, z);
				ItemStack is = new ItemStack(blockId, 1, dmg);
				try {
					name = is.getDisplayName();
				} catch (Exception e) {
					try {
						name = is.getItemName();
					} catch (Exception e2) {
					}
				}
				if (name == null || name.equals("")) {
					name = te.getClass().getSimpleName();
				}
			}
		}
		if (name == null || name.equals("")) {
			name = "peripheral";
		}
		name = name.replaceAll("[^a-zA-Z0-9]", "_");
		name = name.toLowerCase();
		return name;
	}

	public static String documentMethod(MethodDeclaration method) {
		return String.format("%s()", method.getLuaName());
	}

	public static String listMethods(List<MethodDeclaration> methods) {
		StringBuilder builder = new StringBuilder();
		Iterator<MethodDeclaration> methodsIterator = methods.iterator();
		while (methodsIterator.hasNext()) {
			MethodDeclaration method = methodsIterator.next();
			builder.append(MiscUtils.documentMethod(method));
			if (!methodsIterator.hasNext()) {
				break;
			}
			builder.append(" ");
		}

		return builder.toString();
	}

	public static Map documentMethods(List<MethodDeclaration> methods) {
		Map map = new HashMap();
		int i = 1;
		for (MethodDeclaration method : methods) {
			HashMap methodMap = new HashMap();
			HashMap args = new HashMap();
			map.put(i++, methodMap);
			methodMap.put("name", method.getLuaName());
			methodMap.put("description", method.getDescription());
			methodMap.put("returnType", method.getReturnType().toString());
			methodMap.put("args", args);
			int j = 1;
			for (Arg arg : method.getRequiredParameters()) {
				HashMap argMap = new HashMap();
				argMap.put("type", arg.type().toString());
				argMap.put("name", arg.name());
				argMap.put("description", arg.description());
				args.put(j++, argMap);
			}
		}
		return map;
	}
}

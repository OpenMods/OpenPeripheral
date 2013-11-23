package openperipheral.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import openperipheral.MethodDeclaration;
import openperipheral.api.Arg;

public class PeripheralUtils {

	public static String getNameForTarget(Object target) {
		String name = "";
		if (target instanceof IInventory) {
			name = ((IInventory)target).getInvName();
		} else if (target instanceof TileEntity) {
			TileEntity te = (TileEntity)target;
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
						name = is.getUnlocalizedName();
					} catch (Exception e2) {}
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
			builder.append(PeripheralUtils.documentMethod(method));
			if (!methodsIterator.hasNext()) {
				break;
			}
			builder.append(" ");
		}

		return builder.toString();
	}

	public static Map<Object, Object> documentMethods(List<MethodDeclaration> methods) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		int i = 1;
		for (MethodDeclaration method : methods) {
			HashMap<Object, Object> methodMap = new HashMap<Object, Object>();
			HashMap<Object, Object> args = new HashMap<Object, Object>();
			map.put(i++, methodMap);
			methodMap.put("name", method.getLuaName());
			methodMap.put("description", method.getDescription());
			methodMap.put("returnType", method.getReturnType().toString());
			methodMap.put("args", args);
			int j = 1;
			for (Arg arg : method.getRequiredParameters()) {
				HashMap<Object, Object> argMap = new HashMap<Object, Object>();
				argMap.put("type", arg.type().toString());
				argMap.put("name", arg.name());
				argMap.put("description", arg.description());
				args.put(j++, argMap);
			}
		}
		return map;
	}
}

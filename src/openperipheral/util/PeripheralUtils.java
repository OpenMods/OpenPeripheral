package openperipheral.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import openperipheral.api.Arg;
import openperipheral.peripheral.MethodDeclaration;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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
		Set<String> methodsInfo = Sets.newIdentityHashSet();
		for (MethodDeclaration method : methods)
			methodsInfo.add(documentMethod(method));

		return Joiner.on(" ").join(methodsInfo);
	}

	public static Map<Integer, Map<String, Object>> documentMethods(List<MethodDeclaration> methods) {
		Map<Integer, Map<String, Object>> result = Maps.newHashMap();
		int i = 1;
		for (MethodDeclaration method : methods) {
			Map<Integer, Map<?, ?>> argsInfo = Maps.newHashMap();

			int j = 1;
			for (Arg arg : method.getRequiredParameters()) {
				Map<String, Object> argMap = Maps.newHashMap();
				argMap.put("type", arg.type().toString());
				argMap.put("name", arg.name());
				argMap.put("description", arg.description());
				argsInfo.put(j++, argMap);
			}

			Map<String, Object> methodInfo = Maps.newHashMap();
			methodInfo.put("name", method.getLuaName());
			methodInfo.put("description", method.getDescription());
			methodInfo.put("returnType", method.getReturnType().toString());
			methodInfo.put("args", argsInfo);

			result.put(i++, methodInfo);
		}
		return result;
	}
}

package openperipheral.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public class NetworkUtils {

	public static Map NBTToMap(NBTTagCompound tagCompound) {
		HashMap map = new HashMap();

		Collection col = tagCompound.getTags();

		Iterator it = col.iterator();

		while (it.hasNext()) {
			NBTBase nbt = (NBTBase)it.next();
			map.put(nbt.getName(), NBTToObj(nbt));

		}

		return map;
	}

	private static Object NBTToObj(NBTBase nbt) {
		switch (nbt.getId()) {
			case 1:
				return ((NBTTagByte)nbt).data;
			case 2:
				return ((NBTTagShort)nbt).data;
			case 3:
				return ((NBTTagInt)nbt).data;
			case 4:
				return ((NBTTagLong)nbt).data;
			case 5:
				return ((NBTTagFloat)nbt).data;
			case 6:
				return ((NBTTagDouble)nbt).data;
			case 7:
				HashMap map = new HashMap();
				byte[] byteArray = ((NBTTagByteArray)nbt).byteArray;
				for (int i = 0; i < byteArray.length; i++) {
					map.put(i + 1, byteArray[i]);
				}
				return map;
			case 8:
				return ((NBTTagString)nbt).data;
			case 9:
				HashMap map1 = new HashMap();
				NBTTagList list = (NBTTagList)nbt;
				for (int i = 0; i < list.tagCount(); i++) {
					map1.put(i + 1, NBTToObj(list.tagAt(i)));
				}
				return map1;
			case 10:
				return NBTToMap((NBTTagCompound)nbt);
			case 11:
				HashMap map3 = new HashMap();
				int[] intArray = ((NBTTagIntArray)nbt).intArray;
				for (int i = 0; i < intArray.length; i++) {
					map3.put(i + 1, intArray[i]);
				}
				return map3;
			default:
				return "";
		}

	}

}

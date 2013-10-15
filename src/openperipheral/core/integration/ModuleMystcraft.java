package openperipheral.core.integration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.mystcraft.AdapterWritingDesk;

public class ModuleMystcraft {
	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterWritingDesk());
	}

	public static void appendMystcraftInfo(Map map, ItemStack stack) {
		if (stack != null) {
			Item item = stack.getItem();
			if (item != null) {
				if (stack.hasTagCompound()) {
					if ("item.myst.page".equals(item.getUnlocalizedName())) {
						addStringFromNBT(map, stack, "symbol", "symbol");
					} else if ("item.myst.linkbook".equals(item.getUnlocalizedName()) || "item.myst.agebook".equals(item.getUnlocalizedName())) {
						addStringFromNBT(map, stack, "destination", "agename");
						addLinkingBookFlags(map, stack);
						addCoordinates(map, stack);
					}
				}
			}
		}
	}

	public static void entityToMap(Entity entity, HashMap map, Vec3 relativePos) {
		// TODO: Add the linking book entity information
	}

	private static void addCoordinates(Map map, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		HashMap<Integer, Integer> pos = new HashMap<Integer, Integer>();
		map.put("spawn", pos);
		pos.put(1,tag.getInteger("SpawnX"));
		pos.put(2,tag.getInteger("SpawnY"));
		pos.put(3,tag.getInteger("SpawnZ"));
		map.put("spawnYaw",tag.getFloat("SpawnYaw"));
	}

	private static void addLinkingBookFlags(Map map, ItemStack stack) {
		Map<String,Boolean> flags = new HashMap<String,Boolean>();
		map.put("flags", flags);
		NBTTagCompound tag = stack.getTagCompound();
		if (tag.hasKey("Flags")){
			for(NBTBase s:(Collection<NBTBase>)tag.getCompoundTag("Flags").getTags()){
				flags.put(s.getName(), Boolean.TRUE);
			}

		}
	}

	private static void addStringFromNBT(Map map, ItemStack stack, String outputName, String nbtTagName) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag.hasKey(nbtTagName)) {
			map.put(outputName, tag.getString(nbtTagName));
		}
	}
}

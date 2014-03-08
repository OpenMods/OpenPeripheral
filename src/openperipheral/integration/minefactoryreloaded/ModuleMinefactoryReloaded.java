package openperipheral.integration.minefactoryreloaded;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import openmods.Mods;
import openperipheral.adapter.AdapterManager;
import openperipheral.api.IIntegrationModule;

public class ModuleMinefactoryReloaded implements IIntegrationModule {

	@Override
	public String getModId() {
		return Mods.MFR;
	}

	@Override
	public void init() {
		// TODO: Nothing to do yet.
	}

	@Override
	public void appendItemInfo(Map<String, Object> map, ItemStack stack) {
		if (stack != null) {
			Item item = stack.getItem();
			if (item != null) {
				if (stack.hasTagCompound()) {
					if ("item.mfr.safarinet.reusable".equals(item.getUnlocalizedName())) {
						boolean empty = !stack.getTagCompound().hasKey("id");
						map.put("isEmpty", empty);
						if (!empty) {
							addStringFromNBT(map, stack, "captured", "id");
							//addMobName(map, stack); // Probably not necessary
						}
					}
				}
			}
		}
	}

	@Override
	public void appendEntityInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {
		// TODO: Nothing to do here yet?
	}

	// Private Methods

	private static void addMobName(Map<String, Object> map, ItemStack stack) {
		String mobName = StatCollector.translateToLocal("entity." + stack.getTagCompound().getString("id") + ".name");
		map.put("mobName", mobName);
	}

	private static void addStringFromNBT(Map<String, Object> map, ItemStack stack, String outputName, String nbtTagName) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag.hasKey(nbtTagName)) {
			map.put(outputName, tag.getString(nbtTagName));
		}
	}
}
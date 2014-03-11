package openperipheral.integration.minefactoryreloaded;

import java.util.Map;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import openmods.Mods;
import openperipheral.adapter.AdapterManager;
import openperipheral.api.IIntegrationModule;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public class ModuleMinefactoryReloaded implements IIntegrationModule {

	@Override
	public String getModId() {
		return Mods.MFR;
	}

	private static final Set<String> safariNets = ImmutableSet.of(
			"item.mfr.safarinet.reusable",
			"item.mfr.safarinet.singleuse",
			"item.mfr.safarinet.jailer"
			);

	@Override
	public void init() {
		AdapterManager.addPeripheralAdapter(new AdapterAutoAnvil());
		AdapterManager.addPeripheralAdapter(new AdapterAutoDisenchanter());
		AdapterManager.addPeripheralAdapter(new AdapterAutoEnchanter());
		AdapterManager.addPeripheralAdapter(new AdapterAutoJukebox());
		AdapterManager.addPeripheralAdapter(new AdapterAutoSpawner());
		AdapterManager.addPeripheralAdapter(new AdapterChronotyper());
		AdapterManager.addPeripheralAdapter(new AdapterChunkLoader());
		AdapterManager.addPeripheralAdapter(new AdapterEjector());
		AdapterManager.addPeripheralAdapter(new AdapterHarvester());
	}

	@Override
	public void appendItemInfo(Map<String, Object> map, ItemStack stack) {
		Preconditions.checkNotNull(stack); // should not happen

		if (isSafariNet(stack)) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag != null && tag.hasKey("id")) {
				map.put("captured", tag.getString("id"));
			}
		}
	}

	private static boolean isSafariNet(ItemStack stack) {
		Item item = stack.getItem();
		return item != null && safariNets.contains(item.getUnlocalizedName());
	}

	@Override
	public void appendEntityInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {}
}

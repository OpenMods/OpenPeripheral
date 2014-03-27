package openperipheral.integration.computercraft;

import static openmods.utils.ReflectionHelper.safeLoad;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import openmods.Mods;
import openmods.utils.ReflectionHelper;
import openmods.utils.ReflectionHelper.SafeClassLoad;
import openperipheral.TypeConversionRegistry;
import openperipheral.api.IIntegrationModule;

import com.google.common.collect.Maps;

import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;

public class ModuleComputerCraft implements IIntegrationModule {
	public static final SafeClassLoad API_CLASS = safeLoad("dan200.computercraft.ComputerCraft");
	public static final SafeClassLoad PRINTOUT_CLASS = safeLoad("dan200.computercraft.shared.media.items.ItemPrintout");
	public static final SafeClassLoad COMPUTER_ITEM_CLASS = safeLoad("dan200.computercraft.shared.computer.items.IComputerItem");
	public static final SafeClassLoad TURTLE_ITEM_CLASS = safeLoad("dan200.computercraft.shared.turtle.items.ITurtleItem");

	@Override
	public String getModId() {
		return Mods.COMPUTERCRAFT;
	}

	@Override
	public void init() {
		PRINTOUT_CLASS.load();
		COMPUTER_ITEM_CLASS.load();
		TURTLE_ITEM_CLASS.load();
	}

	@Override
	public void appendEntityInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {}

	@Override
	public void appendItemInfo(Map<String, Object> map, ItemStack stack) {
		Item item = stack.getItem();

		if (COMPUTER_ITEM_CLASS.get().isInstance(item)) {
			addComputerInfo(map, stack, item);
		}
		if (item instanceof IMedia) {
			addDiskInfo(map, stack, (IMedia)item);
		} else {
			IMedia media = ReflectionHelper.callStatic(API_CLASS.get(), "getMedia", stack);
			if (media != null) addDiskInfo(map, stack, media);
		}

		if (PRINTOUT_CLASS.get().isInstance(item)) {
			addPrintoutInfo(map, stack, item);
		}
	}

	private static void addPrintoutInfo(Map<String, Object> map, ItemStack stack, Item item) {
		Map<String, Object> printoutMap = Maps.newHashMap();

		printoutMap.put("title", ReflectionHelper.call(item, "getTitle", stack));
		printoutMap.put("pages", ReflectionHelper.call(item, "getPageCount", stack));
		String[] texts = ReflectionHelper.call(item, "getText", stack);

		printoutMap.put("text", TypeConversionRegistry.toLua(texts));

		map.put("printout", printoutMap);
	}

	private static void addDiskInfo(Map<String, Object> map, ItemStack stack, IMedia item) {
		Map<String, Object> diskInfo = Maps.newHashMap();
		String label = item.getLabel(stack);
		if (label != null) diskInfo.put("label", label);

		String record = item.getAudioRecordName(stack);
		if (record != null) diskInfo.put("record", record);

		map.put("disk", diskInfo);
	}

	private static void addComputerInfo(Map<String, Object> map, ItemStack stack, Item item) {
		Map<String, Object> computerInfo = Maps.newHashMap();
		int computerID = ReflectionHelper.call(item, "getComputerID", stack);
		if (computerID >= 0) {
			computerInfo.put("id", computerID);
			String label = ReflectionHelper.call(item, "getLabel", stack);
			if (label != null) computerInfo.put("label", label);
		}

		computerInfo.put("type", ReflectionHelper.call(item, "getFamily", stack));

		if (TURTLE_ITEM_CLASS.get().isInstance(item)) addTurtleInfo(computerInfo, stack, item);

		map.put("computer", computerInfo);
	}

	private static void addTurtleInfo(Map<String, Object> map, ItemStack stack, Item item) {
		addSideInfo(map, "left", ReflectionHelper.<ITurtleUpgrade> call(item, "getUpgrade", stack, TurtleSide.Left));
		addSideInfo(map, "right", ReflectionHelper.<ITurtleUpgrade> call(item, "getUpgrade", stack, TurtleSide.Right));

		int fuelLevel = ReflectionHelper.call(item, "getFuelLevel", stack);
		map.put("fuel", fuelLevel);
	}

	private static void addSideInfo(Map<String, Object> map, String side, ITurtleUpgrade upgrade) {
		if (upgrade != null) {
			Map<Object, Object> upgradeMap = Maps.newHashMap();

			upgradeMap.put("adjective", upgrade.getAdjective());
			upgradeMap.put("type", upgrade.getType().toString());

			map.put(side, upgradeMap);
		}
	}

}

package openperipheral.integration.computercraft;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import openmods.Mods;
import openmods.utils.ReflectionHelper;
import openperipheral.TypeConversionRegistry;
import openperipheral.api.IIntegrationModule;

import com.google.common.collect.Maps;

import dan200.computercraft.api.turtle.ITurtleUpgrade;

public class ModuleComputerCraft implements IIntegrationModule {

	private Class<?> itemComputerBaseClass;
	private Class<?> itemTurtleClass;
	private Class<?> itemDiskClass;
	private Class<?> itemTreasureClass;
	private Class<?> itemPrintoutClass;

	@Override
	public String getModId() {
		return Mods.COMPUTERCRAFT;
	}

	@Override
	public void init() {
		itemComputerBaseClass = ReflectionHelper.getClass("dan200.computer.shared.ItemComputerBase");
		itemTurtleClass = ReflectionHelper.getClass("dan200.turtle.shared.ItemTurtle");
		itemDiskClass = ReflectionHelper.getClass("dan200.computer.shared.ItemDisk");
		itemTreasureClass = ReflectionHelper.getClass("dan200.computer.shared.ItemTreasureDisk");
		itemPrintoutClass = ReflectionHelper.getClass("dan200.computer.shared.ItemPrintout");
	}

	@Override
	public void appendEntityInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {}

	@Override
	public void appendItemInfo(Map<String, Object> map, ItemStack stack) {
		Item item = stack.getItem();

		if (itemComputerBaseClass.isInstance(item)) {
			addComputerInfo(map, stack, item);
		}
		if (itemDiskClass.isInstance(item)) {
			addDiskInfo(map, stack, item);
		}
		if (itemPrintoutClass.isInstance(item)) {
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

	private void addDiskInfo(Map<String, Object> map, ItemStack stack, Item item) {
		HashMap<String, Object> diskInfo = Maps.newHashMap();
		String label = ReflectionHelper.call(item, "getLabel", stack);
		if (label != null) {
			diskInfo.put("label", label);
		}
		if (itemTreasureClass.isInstance(item)) {
			diskInfo.put("treasure", true);
			diskInfo.put("title", ReflectionHelper.call(item, "getTitle", stack));
		} else {
			diskInfo.put("treasure", false);
		}
		map.put("disk", diskInfo);
	}

	private void addComputerInfo(Map<String, Object> map, ItemStack stack, Item item) {
		Map<String, Object> computerInfo = Maps.newHashMap();
		int computerID = ReflectionHelper.call(item, "getComputerIDFromItemStack", stack);
		if (computerID >= 0) {
			computerInfo.put("id", computerID);

			String label = ReflectionHelper.callStatic(itemComputerBaseClass, "getComputerLabelOnServer", ReflectionHelper.primitive(computerID));

			if (label != null) {
				computerInfo.put("label", label);
			}
		}

		computerInfo.put("isAdvanced", ReflectionHelper.call(item, "isItemAdvanced", stack));

		if (itemTurtleClass.isInstance(item)) {
			addTurtleInfo(computerInfo, stack, item);
		}

		map.put("computer", computerInfo);
	}

	private void addTurtleInfo(Map<String, Object> map, ItemStack stack, Item item) {
		addSideInfo(map, "left", (ITurtleUpgrade)ReflectionHelper.call(itemTurtleClass, item, "getLeftUpgradeFromItemStack", stack));
		addSideInfo(map, "right", (ITurtleUpgrade)ReflectionHelper.call(itemTurtleClass, item, "getRightUpgradeFromItemStack", stack));

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

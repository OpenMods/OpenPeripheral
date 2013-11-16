package cofh.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

/**
 * This is effectively a wrapper for Forge Configurations. It allows for easier manipulation of Config files.
 * 
 * @author King Lemming
 * 
 */
public class ConfigHandler {

	ArrayList<String>[] blockEntries = new ArrayList[3];
	ArrayList<String>[] itemEntries = new ArrayList[3];

	TreeMap<String, Property> blockIds = new TreeMap();
	TreeMap<String, Property> itemIds = new TreeMap();

	int blockIdCounter = 0;
	int itemIdCounter = 0;
	int moduleCounter = 0;

	Set assignedIds = new HashSet();

	Configuration modConfiguration;
	String modVersion;

	int blockIdStart = 1000;
	int itemIdStart = 10000;

	public ConfigHandler(String version) {

		modVersion = version;
		for (int i = 0; i < blockEntries.length; i++) {
			blockEntries[i] = new ArrayList();
		}
		for (int i = 0; i < itemEntries.length; i++) {
			itemEntries[i] = new ArrayList();
		}
	}

	public ConfigHandler(String version, int blockStart, int itemStart) {

		modVersion = version;
		blockIdStart = blockStart;
		itemIdStart = itemStart;

		for (int i = 0; i < blockEntries.length; i++) {
			blockEntries[i] = new ArrayList();
		}
		for (int i = 0; i < itemEntries.length; i++) {
			itemEntries[i] = new ArrayList();
		}
	}

	public void setConfiguration(Configuration config) {

		modConfiguration = config;
		modConfiguration.load();
	}

	public Configuration getConfiguration() {

		return modConfiguration;
	}

	public String getVersion() {

		return modVersion;
	}

	public void addBlockEntry(String name) {

		addBlockEntry(name, 0);
	}

	public void addItemEntry(String name) {

		addItemEntry(name, 0);
	}

	public void addBlockEntry(String name, int level) {

		blockEntries[level].add(name);
		blockIdCounter++;
	}

	public void addItemEntry(String name, int level) {

		itemEntries[level].add(name);
		itemIdCounter++;
	}

	public int getBlockId(String name) {

		Property ret = blockIds.get(name);

		if (ret == null) {
			return -1;
		}
		return ret.getInt();
	}

	public int getItemId(String name) {

		Property ret = itemIds.get(name);

		if (ret == null) {
			return -1;
		}
		return ret.getInt();
	}

	public int get(String category, String key, int defaultValue) {

		return modConfiguration.get(category, key, defaultValue).getInt();
	}

	public boolean get(String category, String key, boolean defaultValue) {

		return modConfiguration.get(category, key, defaultValue).getBoolean(defaultValue);
	}

	public String get(String category, String key, String defaultValue) {

		return modConfiguration.get(category, key, defaultValue).getString();
	}

	public Property getProperty(String category, String key, int defaultValue) {

		return modConfiguration.get(category, key, defaultValue);
	}

	public Property getProperty(String category, String key, boolean defaultValue) {

		return modConfiguration.get(category, key, defaultValue);
	}

	public Property getProperty(String category, String key, String defaultValue) {

		return modConfiguration.get(category, key, defaultValue);
	}

	public ConfigCategory getCategory(String category) {

		return modConfiguration.getCategory(category);
	}

	public boolean hasCategory(String category) {

		return modConfiguration.hasCategory(category);
	}

	public boolean hasKey(String category, String key) {

		return modConfiguration.hasKey(category, key);
	}

	public void init() {

		// get ids for existing blocks
		for (int i = 0; i < blockEntries.length; ++i) {
			for (String entry : blockEntries[i]) {
				if (modConfiguration.hasKey(Configuration.CATEGORY_BLOCK, entry)) {
					int existingId = modConfiguration.getCategory(Configuration.CATEGORY_BLOCK).getValues().get(entry).getInt();
					assignedIds.add(existingId);
					blockIds.put(entry, modConfiguration.getBlock(entry, existingId));
				}
			}
		}
		// get ids for new blocks
		for (int i = 0; i < blockEntries.length; ++i) {
			for (String entry : blockEntries[i]) {
				if (!modConfiguration.hasKey(Configuration.CATEGORY_BLOCK, entry)) {
					boolean idFound = false;
					for (int j = blockIdStart; j < blockIdStart + blockIdCounter && !idFound; ++j) {
						if (!assignedIds.contains(j)) {
							assignedIds.add(j);
							blockIds.put(entry, modConfiguration.getBlock(entry, j));
							idFound = true;
						}
					}
				}
			}
		}
		// get ids for existing items
		for (int i = 0; i < itemEntries.length; ++i) {
			for (String entry : itemEntries[i]) {
				if (modConfiguration.hasKey(Configuration.CATEGORY_ITEM, entry)) {
					int existingId = modConfiguration.getCategory(Configuration.CATEGORY_ITEM).getValues().get(entry).getInt();
					assignedIds.add(existingId);
					itemIds.put(entry, modConfiguration.getItem(entry, existingId));
				}
			}
		}
		// get ids for new items
		for (int i = 0; i < itemEntries.length; ++i) {
			for (String entry : itemEntries[i]) {
				if (!modConfiguration.hasKey(Configuration.CATEGORY_ITEM, entry)) {

					boolean idFound = false;
					for (int j = itemIdStart; j < itemIdStart + itemIdCounter && !idFound; ++j) {
						if (!assignedIds.contains(j)) {

							assignedIds.add(j);
							itemIds.put(entry, modConfiguration.getItem(entry, j));
							idFound = true;
						}
					}
				}
			}
		}
		modConfiguration.save();
	}

	public void save() {

		modConfiguration.save();
	}

	public boolean renameProperty(String category, String key, String newCategory, String newKey, boolean forceValue) {

		if (modConfiguration.hasKey(category, key)) {
			Property prop = modConfiguration.getCategory(category).get(key);

			if (prop.isIntValue()) {
				int value = modConfiguration.getCategory(category).getValues().get(key).getInt();
				removeProperty(category, key);

				if (forceValue) {
					removeProperty(newCategory, newKey);
				}
				modConfiguration.get(newCategory, newKey, value);
			} else if (prop.isBooleanValue()) {
				boolean value = modConfiguration.getCategory(category).getValues().get(key).getBoolean(false);
				removeProperty(category, key);

				if (forceValue) {
					removeProperty(newCategory, newKey);
				}
				modConfiguration.get(newCategory, newKey, value);
			} else if (prop.isDoubleValue()) {
				double value = modConfiguration.getCategory(category).getValues().get(key).getDouble(0.0);
				removeProperty(category, key);

				if (forceValue) {
					removeProperty(newCategory, newKey);
				}
				modConfiguration.get(newCategory, newKey, value);
			} else {
				String value = modConfiguration.getCategory(category).getValues().get(key).getString();
				removeProperty(category, key);

				if (forceValue) {
					removeProperty(newCategory, newKey);
				}
				modConfiguration.get(newCategory, newKey, value);
			}
			return true;
		}
		return false;
	}

	public boolean removeProperty(String category, String key) {

		if (!modConfiguration.hasKey(category, key)) {
			return false;
		}
		modConfiguration.getCategory(category).remove(key);
		return true;
	}

	public boolean renameCategory(String category, String newCategory) {

		if (!modConfiguration.hasCategory(category)) {
			return false;
		}
		for (Property prop : modConfiguration.getCategory(category).values()) {
			renameProperty(category, prop.getName(), newCategory, prop.getName(), true);
		}
		removeCategory(category);
		return true;
	}

	public boolean removeCategory(String category) {

		if (!modConfiguration.hasCategory(category)) {
			return false;
		}
		modConfiguration.removeCategory(modConfiguration.getCategory(category));
		return true;
	}

	public void cleanUp(boolean delConfig) {

		removeProperty("general", "version");
		removeProperty("general", "Version");
		get("general", "Version", modVersion);

		modConfiguration.save();

		for (int i = 0; i < blockEntries.length; ++i) {
			blockEntries[i].clear();
		}
		blockEntries = null;

		for (int i = 0; i < itemEntries.length; ++i) {
			itemEntries[i].clear();
		}
		itemEntries = null;

		blockIds.clear();
		itemIds.clear();
		assignedIds.clear();

		if (delConfig) {
			modConfiguration = null;
		}
	}

}

package openperipheral;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import openperipheral.adapter.FeatureGroupManager;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.*;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfigEntries.IConfigEntry;
import cpw.mods.fml.client.config.GuiEditArrayEntries.IArrayEntry;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ConfigGuiFactory implements IModGuiFactory {

	// Clone of settings, since changes have to be applied after restart
	private static FeatureGroupManager CONFIG_FEATURE_GROUP_MANAGER;

	private static class FeatureConfigElement implements IConfigElement<Boolean> {
		private final String architecture;
		private final String featureGroup;

		private FeatureConfigElement(String architecture, String feature) {
			this.architecture = architecture;
			this.featureGroup = feature;
		}

		@Override
		public boolean isProperty() {
			return true;
		}

		@Override
		public Class<? extends IConfigEntry> getConfigEntryClass() {
			return null;
		}

		@Override
		public Class<? extends IArrayEntry> getArrayEntryClass() {
			return null;
		}

		@Override
		public String getName() {
			return featureGroup;
		}

		@Override
		public String getQualifiedName() {
			return featureGroup;
		}

		@Override
		public String getLanguageKey() {
			return featureGroup;
		}

		@Override
		public String getComment() {
			return I18n.format("openperipheralcore.config.featureGroupToggle.tooltip");
		}

		@Override
		public List<IConfigElement> getChildElements() {
			return null;
		}

		@Override
		public ConfigGuiType getType() {
			return ConfigGuiType.BOOLEAN;
		}

		@Override
		public boolean isList() {
			return false;
		}

		@Override
		public boolean isListLengthFixed() {
			return false;
		}

		@Override
		public int getMaxListLength() {
			return -1;
		}

		@Override
		public boolean isDefault() {
			return true;
		}

		@Override
		public Object getDefault() {
			// use actually used setting as default
			return FeatureGroupManager.INSTANCE.isEnabled(featureGroup, architecture);
		}

		@Override
		public Object[] getDefaults() {
			return null;
		}

		@Override
		public void setToDefault() {}

		@Override
		public boolean requiresWorldRestart() {
			return false;
		}

		@Override
		public boolean showInGui() {
			return true;
		}

		@Override
		public boolean requiresMcRestart() {
			return true;
		}

		@Override
		public Object get() {
			return CONFIG_FEATURE_GROUP_MANAGER.isEnabled(featureGroup, architecture);
		}

		@Override
		public Object[] getList() {
			return null;
		}

		@Override
		public void set(Boolean value) {
			if (value == Boolean.TRUE) CONFIG_FEATURE_GROUP_MANAGER.enable(featureGroup, architecture);
			else CONFIG_FEATURE_GROUP_MANAGER.disable(featureGroup, architecture);
		}

		@Override
		public void set(Boolean[] aVal) {}

		@Override
		public String[] getValidValues() {
			return null;
		}

		@Override
		public Boolean getMinValue() {
			return Boolean.FALSE;
		}

		@Override
		public Boolean getMaxValue() {
			return Boolean.TRUE;
		}

		@Override
		public Pattern getValidationPattern() {
			return null;
		}
	}

	public static class ConfigScreen extends GuiConfig {

		public ConfigScreen(GuiScreen parent) {
			super(parent, createConfigElements(), ModInfo.ID, false, true, "Config");
		}

		private static List<IConfigElement> createConfigElements() {
			final List<IConfigElement> result = Lists.newArrayList();
			result.add(createFeatureGroupConfig());
			result.add(createModConfig());
			return result;
		}

		private static IConfigElement createModConfig() {
			final Configuration config = OpenPeripheralCore.instance.config();
			final List<IConfigElement> result = Lists.newArrayList();

			for (String categoryName : config.getCategoryNames()) {
				if (!categoryName.equalsIgnoreCase(Config.CATEGORY_FEATURE_GROUPS)) {
					ConfigCategory category = config.getCategory(categoryName);
					result.add(new ConfigElement(category));
				}
			}

			return new DummyCategoryElement("modConfig", "openperipheralcore.config.miscConfig", result);
		}

		private static IConfigElement createFeatureGroupConfig() {
			final List<IConfigElement> architecturesConfig = Lists.newArrayList();

			for (String architecture : ArchitectureChecker.INSTANCE.knownArchitectures())
				architecturesConfig.add(createArchitectureConfig(architecture));

			return new DummyCategoryElement("featureGroups", "openperipheralcore.config.featureGroupConfig", architecturesConfig);
		}

		private static IConfigElement createArchitectureConfig(final String architecture) {
			final List<IConfigElement> architectureConfig = Lists.newArrayList();

			for (final String feature : FeatureGroupManager.INSTANCE.knownFeatureGroups()) {
				architectureConfig.add(new FeatureConfigElement(architecture, feature));
			}

			return new DummyCategoryElement(architecture, "openperipheralcore.config.architectureConfig", architectureConfig);
		}
	}

	public static class ConfigChangeListener {

		private final Configuration config;

		public ConfigChangeListener(Configuration config) {
			this.config = config;
		}

		@SubscribeEvent
		public void onConfigChange(OnConfigChangedEvent evt) {
			if (ModInfo.ID.equals(evt.modID)) {
				final String[] blacklist = CONFIG_FEATURE_GROUP_MANAGER.saveBlacklist();
				config.get(Config.CATEGORY_FEATURE_GROUPS, Config.FIELD_FEATURE_GROUPS, blacklist).set(blacklist);
				config.save();
			}
		}
	}

	@Override
	public void initialize(Minecraft minecraftInstance) {
		CONFIG_FEATURE_GROUP_MANAGER = FeatureGroupManager.INSTANCE.copy();
	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return ConfigScreen.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return ImmutableSet.of();
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}

}

package openperipheral;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import openmods.Log;
import openperipheral.api.IIntegrationModule;
import openperipheral.integration.vanilla.ModuleVanilla;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.Loader;

public class IntegrationModuleRegistry {

	private static List<IIntegrationModule> registeredModules = Lists.newArrayList();

	private static Map<String, IIntegrationModule> selectedModules = ImmutableMap.of();

	public static void registerModule(IIntegrationModule module) {
		registeredModules.add(module);
	}

	public static void selectLoadedModules() {
		ImmutableMap.Builder<String, IIntegrationModule> builder = ImmutableMap.builder();

		builder.put("vanilla", new ModuleVanilla());

		for (IIntegrationModule module : registeredModules) {
			String modId = module.getModId();
			Log.info("Enabling module %s for %s ", module, modId);
			if (Loader.isModLoaded(modId)) builder.put(modId, module);
		}

		selectedModules = builder.build();
	}

	public static Collection<IIntegrationModule> loadedModules() {
		return selectedModules.values();
	}

	public static void initAllModules() {
		for (IIntegrationModule mod : IntegrationModuleRegistry.loadedModules()) {
			try {
				mod.init();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void appendEntityInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {
		for (IIntegrationModule mod : IntegrationModuleRegistry.loadedModules())
			mod.appendEntityInfo(map, entity, relativePos);
	}

	public static void appendItemInfo(Map<String, Object> map, ItemStack item) {
		for (IIntegrationModule mod : IntegrationModuleRegistry.loadedModules())
			mod.appendItemInfo(map, item);
	}
}

package openperipheral;

import java.util.*;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import openmods.Log;
import openperipheral.api.IIntegrationModule;
import openperipheral.integration.vanilla.ModuleVanilla;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.Loader;

public class IntegrationModuleRegistry {

	private static List<IIntegrationModule> registeredModules = Lists.newArrayList();

	private static Map<String, IIntegrationModule> selectedModules = Maps.newHashMap();

	public static void registerModule(IIntegrationModule module) {
		registeredModules.add(module);
	}

	public static void selectLoadedModules() {
		Set<String> blacklist = Sets.newHashSet();
		for (String modId : Config.blacklist)
			blacklist.add(modId.toLowerCase());

		if (!blacklist.contains(ModuleVanilla.DUMMY_VANILLA_MODID)) selectedModules.put(ModuleVanilla.DUMMY_VANILLA_MODID, new ModuleVanilla());

		for (IIntegrationModule module : registeredModules) {
			String modId = module.getModId();
			if (Loader.isModLoaded(modId)) {
				if (blacklist.contains(modId.toLowerCase())) {
					Log.info("Mod %s is loaded, but integration not enabled due to blacklist", modId);
				} else {
					Log.info("Enabling module %s for %s ", module, modId);
					selectedModules.put(modId, module);
				}
			}
		}
	}

	public static Collection<IIntegrationModule> loadedModules() {
		return selectedModules.values();
	}

	private interface ModuleVisitor {
		public String getOperation();

		public void visit(IIntegrationModule module);
	}

	private static void visitModules(ModuleVisitor visitor) {
		Iterator<Map.Entry<String, IIntegrationModule>> it = selectedModules.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, IIntegrationModule> e = it.next();
			try {
				visitor.visit(e.getValue());
			} catch (Throwable t) {
				Log.warn(t, "Integration module '%s' failed during operation '%s' and will be disabled", e.getKey(), visitor.getOperation());
				it.remove();
			}
		}
	}

	public static void initAllModules() {
		visitModules(new ModuleVisitor() {
			@Override
			public void visit(IIntegrationModule module) {
				module.init();
			}

			@Override
			public String getOperation() {
				return "init";
			}
		});
	}

	public static void appendEntityInfo(final Map<String, Object> map, final Entity entity, final Vec3 relativePos) {
		visitModules(new ModuleVisitor() {
			@Override
			public void visit(IIntegrationModule module) {
				module.appendEntityInfo(map, entity, relativePos);
			}

			@Override
			public String getOperation() {
				return "appendEntityInfo";
			}
		});
	}

	public static void appendItemInfo(final Map<String, Object> map, final ItemStack item) {
		visitModules(new ModuleVisitor() {
			@Override
			public void visit(IIntegrationModule module) {
				module.appendItemInfo(map, item);
			}

			@Override
			public String getOperation() {
				return "appendItemInfo";
			}
		});
	}
}

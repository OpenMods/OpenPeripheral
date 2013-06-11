package openperipheral.common;

import net.minecraft.src.ModLoader;
import net.minecraftforge.common.MinecraftForge;
import openperipheral.OpenPeripheral;
import openperipheral.common.block.BlockGlassesBridge;
import openperipheral.common.core.Mods;
import openperipheral.common.integration.mps.GlassesModule;
import openperipheral.common.item.ItemGlasses;
import openperipheral.common.util.LanguageUtils;
import openperipheral.common.util.RecipeUtils;
import openperipheral.common.util.ReflectionHelper;

public class CommonProxy {

	public void init() {

		OpenPeripheral.Items.glasses = new ItemGlasses();
		OpenPeripheral.Blocks.glassesBridge = new BlockGlassesBridge();

		setupLanguages();

		RecipeUtils.addGlassesRecipe();
		RecipeUtils.addBridgeRecipe();
		RecipeUtils.addBookRecipe();

		MinecraftForge.EVENT_BUS.register(new ChatCommandInterceptor());
		
		if (ModLoader.isModLoaded(Mods.MPS)) {
			ReflectionHelper.callMethod("net.machinemuse.api.ModuleManager", null, new String[] { "addModule"} , new GlassesModule());
		}
	}

	public void registerRenderInformation() {
	}

	private void setupLanguages() {
		LanguageUtils.setupLanguages();
	}
}

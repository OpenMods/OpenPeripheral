package openperipheral.common;

import net.minecraftforge.common.MinecraftForge;
import openperipheral.OpenPeripheral;
import openperipheral.common.block.BlockGlassesBridge;
import openperipheral.common.block.BlockProxy;
import openperipheral.common.core.Mods;
import openperipheral.common.item.ItemGlasses;
import openperipheral.common.util.LanguageUtils;
import openperipheral.common.util.MPSUtils;
import openperipheral.common.util.RecipeUtils;
import cpw.mods.fml.common.Loader;

public class CommonProxy {

	public void init() {

		OpenPeripheral.Items.glasses = new ItemGlasses();
		OpenPeripheral.Blocks.glassesBridge = new BlockGlassesBridge();
		OpenPeripheral.Blocks.proxy = new BlockProxy();

		setupLanguages();

		RecipeUtils.addGlassesRecipe();
		RecipeUtils.addBridgeRecipe();
		RecipeUtils.addBookRecipe();

		MinecraftForge.EVENT_BUS.register(new ChatCommandInterceptor());
		
		if (Loader.isModLoaded(Mods.MPS)) {
			MPSUtils.initModule();
		}
	}

	public void registerRenderInformation() {
	}

	private void setupLanguages() {
		LanguageUtils.setupLanguages();
	}
}

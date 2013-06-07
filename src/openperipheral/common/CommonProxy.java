package openperipheral.common;

import net.minecraftforge.common.MinecraftForge;
import openperipheral.OpenPeripheral;
import openperipheral.common.block.BlockGlassesBridge;
import openperipheral.common.item.ItemGlasses;
import openperipheral.common.terminal.DrawableManager;
import openperipheral.common.util.LanguageUtils;
import openperipheral.common.util.RecipeUtils;

public class CommonProxy {

	public void init() {
		OpenPeripheral.Items.glasses = new ItemGlasses();
		OpenPeripheral.Blocks.glassesBridge = new BlockGlassesBridge();
		setupLanguages();
		RecipeUtils.addGlassesRecipe();
		RecipeUtils.addBridgeRecipe();
		RecipeUtils.addBookRecipe();
		
		MinecraftForge.EVENT_BUS.register(new ChatCommandInterceptor());
		
	}

	public void registerRenderInformation() {
	}

	private void setupLanguages() {
		LanguageUtils.setupLanguages();
	}
	

	public DrawableManager getDrawableManager() {
		return null;
	}

}

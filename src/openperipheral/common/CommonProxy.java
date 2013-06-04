package openperipheral.common;

import openperipheral.OpenPeripheral;
import openperipheral.common.block.BlockGlassesBridge;
import openperipheral.common.item.ItemGlasses;
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
	}

	public void registerRenderInformation() {

	}

	private void setupLanguages() {
		LanguageUtils.setupLanguages();
	}

}

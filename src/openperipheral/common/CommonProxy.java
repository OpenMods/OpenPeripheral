package openperipheral.common;

import java.util.Collections;
import net.minecraft.src.ModLoader;
import net.minecraftforge.common.MinecraftForge;
import openperipheral.Mods;
import openperipheral.OpenPeripheral;
import openperipheral.common.block.BlockGlassesBridge;
import openperipheral.common.item.ItemGlasses;
import openperipheral.common.terminal.DrawableManager;
import openperipheral.common.util.FontSizeChecker;
import openperipheral.common.util.LanguageUtils;
import openperipheral.common.util.RecipeUtils;
import openperipheral.mps.GlassesModule;

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
			net.machinemuse.api.ModuleManager.addModule(
					new GlassesModule()
			);
			
		}
	}
	
	public FontSizeChecker getFontSizeChecker() {
		return null;
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

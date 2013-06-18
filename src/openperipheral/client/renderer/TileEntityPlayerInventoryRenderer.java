package openperipheral.client.renderer;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import openperipheral.client.model.ModelPlayerInventory;
import openperipheral.common.config.ConfigSettings;
import openperipheral.common.tileentity.TileEntityPlayerInventory;

import org.lwjgl.opengl.GL11;

public class TileEntityPlayerInventoryRenderer extends TileEntitySpecialRenderer {

	protected ModelPlayerInventory model = new ModelPlayerInventory();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.0f, (float) z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		bindTextureByName(ConfigSettings.TEXTURES_PATH + "/models/playerinventory.png");
		model.render(((TileEntityPlayerInventory) tileentity).hasPlayer());
		GL11.glPopMatrix();
	}

}

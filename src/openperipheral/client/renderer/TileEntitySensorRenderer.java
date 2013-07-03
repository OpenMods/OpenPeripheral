package openperipheral.client.renderer;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import openperipheral.client.model.ModelSensor;
import openperipheral.common.config.ConfigSettings;
import openperipheral.common.tileentity.TileEntitySensor;

import org.lwjgl.opengl.GL11;

public class TileEntitySensorRenderer extends TileEntitySpecialRenderer {

	private ModelSensor modelSensor = new ModelSensor();

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick) {

		TileEntitySensor sensor = (TileEntitySensor) tileEntity;
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
		int rotation = (int) sensor.getRotation();
		GL11.glPushMatrix();
		this.bindTextureByName(String.format("%s/models/sensor.png",ConfigSettings.TEXTURES_PATH));
		this.modelSensor.renderSensor(rotation);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();

	}
}

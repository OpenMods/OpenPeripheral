package openperipheral.client.renderer;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import openperipheral.client.model.ModelLazer;
import openperipheral.common.config.ConfigSettings;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderLazer extends Render {

	private ModelLazer model = new ModelLazer();
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime) {
		GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        this.loadTexture(String.format("%s/models/lazer.png", ConfigSettings.TEXTURES_PATH));
        model.render(entity);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
	}
	
}

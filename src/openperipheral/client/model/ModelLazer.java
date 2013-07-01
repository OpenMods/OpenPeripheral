package openperipheral.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelLazer extends ModelBase {
	
	ModelRenderer laser;

	public ModelLazer() {
		textureWidth = 32;
		textureHeight = 32;

		laser = new ModelRenderer(this, 0, 0);
		laser.addBox(-0.1F, -0.1F, 3F, 2, 2, 7);
		laser.setRotationPoint(0F, 0F, 0F);
		laser.setTextureSize(32, 32);
		laser.mirror = true;
		setRotation(laser, 0F, 0F, 0F);
	}

	public void render(Entity entity) {
		setRotationAngles(entity);
		laser.render(0.0625F);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(Entity par7Entity) {
		laser.rotateAngleX = (float) Math.toRadians(par7Entity.rotationPitch);
	}
}

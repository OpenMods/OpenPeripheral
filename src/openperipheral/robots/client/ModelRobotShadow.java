package openperipheral.robots.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelRobotShadow extends ModelBase {

	ModelRenderer body;
	ModelRenderer wing1;
	ModelRenderer wing2;

	public ModelRobotShadow() {
		textureWidth = 64;
		textureHeight = 32;

		body = new ModelRenderer(this, 0, 22);
		body.addBox(-3F, -2F, -3F, 6, 4, 6);
		body.setRotationPoint(0F, 22F, 0F);
		body.setTextureSize(64, 32);
		body.mirror = true;
		setRotation(body, 0F, 0F, 0F);
		wing1 = new ModelRenderer(this, 0, 4);
		wing1.addBox(2F, 0F, 0F, 6, 1, 3);
		wing1.setRotationPoint(0F, 22F, -2F);
		wing1.setTextureSize(64, 32);
		wing1.mirror = true;
		setRotation(wing1, 0.3490659F, -0.3490659F, 0F);
		wing2 = new ModelRenderer(this, 0, 0);
		wing2.addBox(-8F, 0F, 0F, 6, 1, 3);
		wing2.setRotationPoint(0F, 22F, -2F);
		wing2.setTextureSize(64, 32);
		wing2.mirror = true;
		setRotation(wing2, 0.3490659F, 0.3490659F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		body.render(f5);
		wing1.render(f5);
		wing2.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {

	}

}

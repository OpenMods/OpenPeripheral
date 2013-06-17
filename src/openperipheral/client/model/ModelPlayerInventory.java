package openperipheral.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelPlayerInventory extends ModelBase {

	float f5 = 0.0625F;
	
	ModelRenderer pressure;
	ModelRenderer chest;

	public ModelPlayerInventory() {
		textureWidth = 64;
		textureHeight = 64;

		pressure = new ModelRenderer(this, 0, 21);
		pressure.addBox(-8F, 0F, -8F, 15, 1, 15);
		pressure.setRotationPoint(0.5F, 10F, 0.5F);
		pressure.setTextureSize(64, 64);
		pressure.mirror = true;
		setRotation(pressure, 0F, 0F, 0F);
		chest = new ModelRenderer(this, 0, 0);
		chest.addBox(-8F, 0F, -8F, 16, 5, 16);
		chest.setRotationPoint(0F, 11F, 0F);
		chest.setTextureSize(64, 64);
		chest.mirror = true;
		setRotation(chest, 0F, 0F, 0F);
	}

	public void render(boolean pressed) {
		pressure.rotationPointY = pressed ? 10.5f : 10f;
		pressure.render(f5);
		chest.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}

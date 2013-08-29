package openperipheral.core.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelBlockFrame extends ModelBase {

	ModelRenderer sideShort;
	ModelRenderer sideLong;

	ModelRenderer[] shortSides = new ModelRenderer[8];
	ModelRenderer[] longSides = new ModelRenderer[4];
	float f5 = 0.0625F;

	float halfPI = (float)Math.PI / 2;

	public ModelBlockFrame() {
		textureWidth = 64;
		textureHeight = 64;

		for (int i = 0; i < shortSides.length; i++) {
			shortSides[i] = new ModelRenderer(this, 4, 30);
			shortSides[i].addBox(-8F, -7F, -8F, 1, 14, 1);
			shortSides[i].setTextureSize(64, 64);
			shortSides[i].mirror = true;
			shortSides[i].setRotationPoint(0F, 8F, 0F);
			float xRot = 0f;
			float yRot = 0f;
			float zRot = halfPI * i;
			if (i >= 4) {
				xRot += halfPI * 2;
			}
			setRotation(shortSides[i], xRot, yRot, zRot);
		}

		for (int i = 0; i < longSides.length; i++) {
			longSides[i] = new ModelRenderer(this, 0, 30);
			longSides[i].addBox(-8F, -8F, -8F, 1, 16, 1);
			longSides[i].setTextureSize(64, 64);
			longSides[i].mirror = true;
			longSides[i].setRotationPoint(0F, 8F, 0F);
		}
		setRotation(longSides[0], halfPI, 0f, 0f);
		setRotation(longSides[1], -halfPI, 0f, 0f);
		setRotation(longSides[2], halfPI, halfPI * 2, 0f);
		setRotation(longSides[3], -halfPI, halfPI * 2, 0f);

	}

	public void render() {
		for (int i = 0; i < shortSides.length; i++) {
			shortSides[i].render(f5);
		}
		for (int i = 0; i < longSides.length; i++) {
			longSides[i].render(f5);
		}
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}

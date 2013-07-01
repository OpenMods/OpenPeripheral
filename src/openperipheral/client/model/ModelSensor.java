package openperipheral.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSensor extends ModelBase {

	public ModelRenderer sensorBase;
	public ModelRenderer sensorAxel;
	public ModelRenderer sensorDishCenter;
	public ModelRenderer sensorDishLeft;
	public ModelRenderer sensorDishRight;

	public ModelSensor() {

		this.sensorBase = new ModelRenderer(this, 0, 0).setTextureSize(64, 64);
		this.sensorBase.addBox(-8.0F, -8.0F, -8.0F, 16, 4, 16, 0.0F);

		this.sensorAxel = new ModelRenderer(this, 0, 0).setTextureSize(64, 64);
		this.sensorAxel.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
		this.sensorAxel.setRotationPoint(0.0F, -4.0F, 0.0F);

		this.sensorDishCenter = new ModelRenderer(this, 0, 24).setTextureSize(64, 64);
		this.sensorDishCenter.addBox(0, 0, 0, 7, 6, 1);
		this.sensorDishCenter.setRotationPoint(-3.0F, 4.0F, -0.5F);

		this.sensorDishLeft = new ModelRenderer(this, 32, 24).setTextureSize(64, 64);
		this.sensorDishLeft.addBox(0.0F, 0.0F, 0.0F, 4, 6, 1, 0.0F);
		this.sensorDishLeft.setRotationPoint(0.0F, 0.0F, 1.0F);
		this.sensorDishLeft.rotateAngleY = -(7.0F / 6.0F) * (float) (Math.PI);

		this.sensorDishRight = new ModelRenderer(this, 48, 24).setTextureSize(64, 64);
		this.sensorDishRight.addBox(3.5F, 0.0F, 0.0F, 4, 6, 1, 0.0F);
		this.sensorDishRight.setRotationPoint(3.5F, 0.0F, 2.0F);
		this.sensorDishRight.rotateAngleY = (float) (Math.PI / 6);

		this.sensorAxel.addChild(sensorDishCenter);
		this.sensorDishCenter.addChild(sensorDishLeft);
		this.sensorDishCenter.addChild(sensorDishRight);

	}

	/**
	 * Renders the sign model through TileEntitySignRenderer
	 */
	public void renderSensor(float degrees) {
		this.sensorBase.render(0.0625F);
		this.sensorAxel.rotateAngleY = (degrees * (float) Math.PI / 180F) % 360;
		this.sensorAxel.render(0.0625F);
	}
}
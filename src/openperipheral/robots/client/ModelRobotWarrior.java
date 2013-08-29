package openperipheral.robots.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import openperipheral.robots.entity.EntityRobotWarrior;

import org.lwjgl.opengl.GL11;

public class ModelRobotWarrior extends ModelBase {
	// fields
	ModelRenderer shoulderright;
	ModelRenderer head;
	ModelRenderer shoulderleft;
	ModelRenderer pelvis;
	ModelRenderer bodybolt;
	ModelRenderer body;
	ModelRenderer leg2;
	ModelRenderer legpart4;
	ModelRenderer leg1;
	ModelRenderer legpart3;
	ModelRenderer legpart1;
	ModelRenderer legpart2;
	ModelRenderer foot2;
	ModelRenderer foot1;
	ModelRenderer longarm;
	ModelRendererGun gun;
	ModelRenderer shortarm;
	ModelRenderer melee;

	public ModelRobotWarrior() {
		textureWidth = 128;
		textureHeight = 32;

		shoulderright = new ModelRenderer(this, 24, 12);
		shoulderright.addBox(3F, -4F, -2F, 6, 4, 4);
		shoulderright.setRotationPoint(0F, 0F, 0F);
		shoulderright.setTextureSize(128, 32);
		shoulderright.mirror = true;
		setRotation(shoulderright, 0.1858931F, 0F, 0F);
		head = new ModelRenderer(this, 40, 20);
		head.addBox(-3F, -6F, -3F, 6, 6, 6);
		head.setRotationPoint(0F, 0F, 0F);
		head.setTextureSize(128, 32);
		head.mirror = true;
		setRotation(head, 0.1570796F, 0F, 0F);
		shoulderleft = new ModelRenderer(this, 44, 12);
		shoulderleft.addBox(-9F, -4F, -2F, 6, 4, 4);
		shoulderleft.setRotationPoint(0F, 0F, 0F);
		shoulderleft.setTextureSize(128, 32);
		shoulderleft.mirror = true;
		setRotation(shoulderleft, 0.1858931F, 0F, 0F);
		pelvis = new ModelRenderer(this, 0, 18);
		pelvis.addBox(-4F, 0F, -1.5F, 8, 2, 3);
		pelvis.setRotationPoint(0F, 5F, 0F);
		pelvis.setTextureSize(128, 32);
		pelvis.mirror = true;
		setRotation(pelvis, 0F, 0F, 0F);
		bodybolt = new ModelRenderer(this, 24, 26);
		bodybolt.addBox(-2F, 0F, -2F, 4, 2, 4);
		bodybolt.setRotationPoint(0F, 0F, 0F);
		bodybolt.setTextureSize(128, 32);
		bodybolt.mirror = true;
		setRotation(bodybolt, 0F, 0F, 0F);
		body = new ModelRenderer(this, 36, 7);
		body.addBox(-1F, 2F, -1F, 2, 3, 2);
		body.setRotationPoint(0F, 0F, 0F);
		body.setTextureSize(128, 32);
		body.mirror = true;
		setRotation(body, 0F, 0F, 0F);
		leg2 = new ModelRenderer(this, 12, 23);
		leg2.addBox(2F, 0F, -1.5F, 3, 6, 3);
		leg2.setRotationPoint(0F, 7F, 0F);
		leg2.setTextureSize(128, 32);
		leg2.mirror = true;
		setRotation(leg2, 0F, 0F, 0F);
		legpart4 = new ModelRenderer(this, 82, 0);
		legpart4.addBox(4.5F, 0F, -1F, 1, 8, 2);
		legpart4.setRotationPoint(0F, 12F, 0F);
		legpart4.setTextureSize(128, 32);
		legpart4.mirror = true;
		setRotation(legpart4, 0F, 0F, 0F);
		leg1 = new ModelRenderer(this, 0, 23);
		leg1.addBox(-5F, 0F, -1.5F, 3, 6, 3);
		leg1.setRotationPoint(0F, 7F, 0F);
		leg1.setTextureSize(128, 32);
		leg1.mirror = true;
		setRotation(leg1, 0F, 0F, 0F);
		legpart3 = new ModelRenderer(this, 76, 0);
		legpart3.addBox(1.5F, 0F, -1F, 1, 8, 2);
		legpart3.setRotationPoint(0F, 12F, 0F);
		legpart3.setTextureSize(128, 32);
		legpart3.mirror = true;
		setRotation(legpart3, 0F, 0F, 0F);
		legpart1 = new ModelRenderer(this, 70, 0);
		legpart1.addBox(-5.5F, 0F, -1F, 1, 8, 2);
		legpart1.setRotationPoint(0F, 12F, 0F);
		legpart1.setTextureSize(128, 32);
		legpart1.mirror = true;
		setRotation(legpart1, 0F, 0F, 0F);
		legpart2 = new ModelRenderer(this, 64, 0);
		legpart2.addBox(-2.5F, 0F, -1F, 1, 8, 2);
		legpart2.setRotationPoint(0F, 12F, 0F);
		legpart2.setTextureSize(128, 32);
		legpart2.mirror = true;
		setRotation(legpart2, 0F, 0F, 0F);
		foot2 = new ModelRenderer(this, 96, 24);
		foot2.addBox(2.5F, 0F, -4.5F, 2, 2, 6);
		foot2.setRotationPoint(0F, 19F, 0F);
		foot2.setTextureSize(128, 32);
		foot2.mirror = true;
		setRotation(foot2, 0F, 0F, 0F);
		foot1 = new ModelRenderer(this, 112, 24);
		foot1.addBox(-4.5F, 0F, -4.5F, 2, 2, 6);
		foot1.setRotationPoint(0F, 19F, 0F);
		foot1.setTextureSize(128, 32);
		foot1.mirror = true;
		setRotation(foot1, 0F, 0F, 0F);
		longarm = new ModelRenderer(this, 0, 0);
		longarm.addBox(9F, -3F, -1.5F, 2, 14, 2);
		longarm.setRotationPoint(0F, 0F, 0F);
		longarm.setTextureSize(128, 32);
		longarm.mirror = true;
		setRotation(longarm, 0F, 0F, 0F);
		gun = new ModelRendererGun(this, 12, 0);
		gun.addBox(-2F, -2F, -9F, 4, 4, 8);
		gun.setRotationPoint(-10F, 2F, 0F);
		gun.setTextureSize(128, 32);
		gun.mirror = true;
		setRotation(gun, 0F, 0F, 0F);
		shortarm = new ModelRenderer(this, 36, 0);
		shortarm.addBox(-11F, -3F, -1F, 2, 5, 2);
		shortarm.setRotationPoint(0F, 0F, -0.5F);
		shortarm.setTextureSize(128, 32);
		shortarm.mirror = true;
		setRotation(shortarm, 0F, 0F, 0F);
		melee = new ModelRenderer(this, 60, 0);
		melee.addBox(11F, 6F, -1F, 1, 10, 1);
		melee.setRotationPoint(0F, 0F, 0F);
		melee.setTextureSize(128, 32);
		melee.mirror = true;
		setRotation(melee, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		GL11.glPushMatrix();
		GL11.glScalef(2, 2, 2);
		GL11.glTranslatef(0.0F, -0.5f, 0.0F);
		shoulderright.render(f5);
		head.render(f5);
		shoulderleft.render(f5);
		longarm.render(f5);
		gun.render(f5);
		shortarm.render(f5);
		melee.render(f5);

		pelvis.render(f5);
		bodybolt.render(f5);
		body.render(f5);
		leg2.render(f5);
		legpart4.render(f5);
		leg1.render(f5);
		legpart3.render(f5);
		legpart1.render(f5);
		legpart2.render(f5);
		foot2.render(f5);
		foot1.render(f5);
		GL11.glPopMatrix();
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {

		EntityRobotWarrior robot = (EntityRobotWarrior)entity;

		head.rotateAngleY = par4 / (180F / (float)Math.PI);
		head.rotateAngleX = par5 / (180F / (float)Math.PI);
		shoulderleft.rotateAngleY = head.rotateAngleY;
		shoulderright.rotateAngleY = head.rotateAngleY;
		longarm.rotateAngleY = head.rotateAngleY;
		melee.rotateAngleY = head.rotateAngleY;
		shortarm.rotateAngleY = head.rotateAngleY;

		shoulderleft.rotateAngleX = head.rotateAngleX;
		shoulderright.rotateAngleX = head.rotateAngleX;
		longarm.rotateAngleX = head.rotateAngleX;
		melee.rotateAngleX = head.rotateAngleX;
		shortarm.rotateAngleX = head.rotateAngleX;

		float z = (float)(head.rotationPointZ + 10.0F * Math.sin(head.rotateAngleY));
		float x = (float)(head.rotationPointX - 10.0F * Math.cos(head.rotateAngleY));

		gun.rotationPointX = x;
		gun.rotationPointZ = z;
		// gun.rotateAngleX = 0;
		gun.rotateAngleY = head.rotateAngleY;
		gun.rotateAngleX = head.rotateAngleX;
		gun.rotateAngleZ = robot.getWeaponSpin();

		par2 *= 1.2;

		leg1.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
		rotateLeg(par1, par2, leg1, legpart1, legpart2, foot1);
		leg2.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.4F * par2;
		rotateLeg(par1, par2, leg2, legpart3, legpart4, foot2);

	}

	private void rotateLeg(float f, float i, ModelRenderer upper, ModelRenderer lower1, ModelRenderer lower2, ModelRenderer foot) {
		float z = (float)(upper.rotationPointZ - 6.0F * Math.cos(upper.rotateAngleX + (Math.PI / 2)));
		float y = (float)(upper.rotationPointY + 6.0F * Math.sin(upper.rotateAngleX + (Math.PI / 2)));
		lower1.rotationPointY = y;
		lower1.rotationPointZ = z;
		lower2.rotationPointY = lower1.rotationPointY;
		lower2.rotationPointZ = lower1.rotationPointZ;
		if (upper.rotateAngleX > 0) {
			lower1.rotateAngleX = (float)(upper.rotateAngleX * 1.5);
		} else {
			lower1.rotateAngleX = 0;
		}
		lower2.rotateAngleX = lower1.rotateAngleX;
		z = (float)(lower1.rotationPointZ - 6.0F * Math.cos(lower1.rotateAngleX + (Math.PI / 2)));
		y = (float)(lower1.rotationPointY + 6.0F * Math.sin(lower1.rotateAngleX + (Math.PI / 2)));
		foot.rotationPointY = y;
		foot.rotationPointZ = z;
	}

}

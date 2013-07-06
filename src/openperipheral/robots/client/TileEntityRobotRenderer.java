package openperipheral.robots.client;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import openperipheral.core.ConfigSettings;
import openperipheral.core.client.ModelBlockFrame;
import openperipheral.robots.block.TileEntityRobot;
import openperipheral.robots.entity.EntityRobot;
import openperipheral.robots.entity.EntityRobotWarrior;

import org.lwjgl.opengl.GL11;

public class TileEntityRobotRenderer extends TileEntitySpecialRenderer {

	private ModelRobotWarrior model = new ModelRobotWarrior();
	private ModelBlockFrame frame = new ModelBlockFrame();
	private EntityRobot robot = new EntityRobotWarrior(null);
	
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
		TileEntityRobot robotTE = (TileEntityRobot) tileEntity;
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.0f, (float) z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		bindTextureByName(ConfigSettings.TEXTURES_PATH + "/models/blockframe.png");
		frame.render();
		GL11.glPushMatrix();
		GL11.glScalef(0.1f, 0.1f, 0.1f);
		GL11.glTranslatef(0, 1f, 0);
		GL11.glRotatef(robotTE.getRenderRot(), 0F, 1.0F, 0.0F);
		bindTextureByName(ConfigSettings.TEXTURES_PATH + "/models/robot.png");
		model.render(robot, 0, 0, 0, 0, 0, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}

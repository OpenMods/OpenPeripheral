package openperipheral.robots.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.ResourceLocation;
import net.minecraft.inventory.Container;
import openperipheral.core.ConfigSettings;
import openperipheral.robots.common.block.TileEntityRobot;

public class GuiRobot extends GuiContainer {

	private static final ResourceLocation background = new ResourceLocation("openperipheral", "textures/gui/robot.png");
	
	public GuiRobot(Container container, TileEntityRobot robot) {
		super(container);
		this.ySize = 168;
	}

	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int left = (this.width - this.xSize) / 2;
		int top = (this.height - this.ySize) / 2;
		this.mc.renderEngine.func_110577_a(background);
		this.drawTexturedModalRect(left, top, 0, 0, this.xSize, this.ySize);
	}

}

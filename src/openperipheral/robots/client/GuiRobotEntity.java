package openperipheral.robots.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import openperipheral.core.container.ContainerGeneric;

import org.lwjgl.opengl.GL11;

public class GuiRobotEntity extends GuiContainer {

	private static final ResourceLocation background = new ResourceLocation("openperipheral", "textures/gui/robotentitygui.png");

	public GuiRobotEntity(ContainerGeneric container) {
		super(container);
		this.ySize = 168;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int left = (this.width - this.xSize) / 2;
		int top = (this.height - this.ySize) / 2;
		this.mc.renderEngine.func_110577_a(background);
		this.drawTexturedModalRect(left, top, 0, 0, this.xSize, this.ySize);
	}
}

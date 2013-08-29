package openperipheral.core.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import openperipheral.core.block.TileEntityTicketMachine;
import openperipheral.core.container.ContainerGeneric;

import org.lwjgl.opengl.GL11;

public class GuiTicketMachine extends GuiContainer {

	private TileEntityTicketMachine ticketMachine;

	private static final ResourceLocation background = new ResourceLocation("openperipheral", "textures/gui/ticketmachine.png");

	public GuiTicketMachine(ContainerGeneric container, TileEntityTicketMachine tileentity) {
		super(container);
		ticketMachine = tileentity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String machineName = StatCollector.translateToLocal("openperipheral.gui.ticketmachine");
		int x = this.xSize / 2 - (fontRenderer.getStringWidth(machineName) / 2);
		fontRenderer.drawString(machineName, x, 6, 4210752);
		String translatedName = StatCollector.translateToLocal("container.inventory");
		fontRenderer.drawString(translatedName, 8, this.ySize - 96 + 2, 4210752);
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

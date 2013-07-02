package openperipheral.client.gui;

import openperipheral.common.tileentity.TileEntityRobot;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

public class GuiRobot extends GuiContainer {

	public GuiRobot(Container container, TileEntityRobot robot) {
		super(container);
	}
	
	@Override
    protected void mouseClicked(int x, int y, int par3)
    {
	    super.mouseClicked(x, y, par3);
	    //this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, buttons[i].index);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		// TODO Auto-generated method stub
		
	}

}

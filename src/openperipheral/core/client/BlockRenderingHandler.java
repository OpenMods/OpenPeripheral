package openperipheral.core.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.world.IBlockAccess;
import openperipheral.OpenPeripheral;
import openperipheral.core.block.TileEntityPlayerInventory;
import openperipheral.robots.block.TileEntityRobot;
import openperipheral.sensor.block.TileEntitySensor;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockRenderingHandler implements ISimpleBlockRenderingHandler {

	private TileEntityRobot teRobot = new TileEntityRobot();
	private TileEntityPlayerInventory tePIM = new TileEntityPlayerInventory();
	private TileEntitySensor teSensor = new TileEntitySensor();
	
	@Override
	public int getRenderId() {
		return OpenPeripheral.renderId;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		if (block == OpenPeripheral.Blocks.playerInventory) {
			TileEntityRenderer.instance.renderTileEntityAt(tePIM, 0.0D, 0.0D, 0.0D, 0.0F);
		}else if (block == OpenPeripheral.Blocks.sensor) {
			TileEntityRenderer.instance.renderTileEntityAt(teSensor, 0.0D, 0.0D, 0.0D, 0.0F);	
		}else if (block == OpenPeripheral.Blocks.robot) {
			TileEntityRenderer.instance.renderTileEntityAt(teRobot, 0.0D, 0.0D, 0.0D, 0.0F);	
		}
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

}
package openperipheral.common.terminal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import openperipheral.common.tileentity.TileEntityGlassesBridge;

import org.lwjgl.opengl.GL11;

public class DrawableBox extends BaseDrawable implements IDrawable {

	private int x;
	private int y;
	private int width;
	private int height;
	private int color;
	private double alpha;
	
	public DrawableBox() {
		super();
	}
	
	public DrawableBox(TileEntityGlassesBridge parent, int x, int y, int width, int height, int color, double alpha) {
		super(parent);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
		this.alpha = alpha;
		this.methodNames = new String[] {
				"getX",
				"setX",
				"getY",
				"setY",
				"getWidth",
				"setWidth",
				"getHeight",
				"setHeight",
				"getColor",
				"setColor",
				"getAlpha",
				"setAlpha",
				"setZIndex",
				"getZIndex",
				"delete"
		};
	}
	
	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public void draw(ItemStack stack, EntityPlayer player,
			ScaledResolution resolution, float partialTicks, boolean hasScreen,
			int mouseX, int mouseY) {
		float r = (float)((color >> 16) & 0xFF) / 255;
		float g = (float)((color >> 8) & 0xFF) / 255;
		float b = (float)(color & 0xFF) / 255;
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4d(r, g, b, alpha);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double)x, (double)y+height, 0.0D);
        tessellator.addVertex((double)x+width, (double)y+height, 0.0D);
        tessellator.addVertex((double)x+width, y, 0.0D);
        tessellator.addVertex((double)x, (double)y, 0.0D);
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
	}
	@Override
	public void writeTo(DataOutputStream stream) {
		try {
			stream.writeShort((short)x);
			stream.writeShort((short)y);
			stream.writeShort((short)width);
			stream.writeShort((short)height);
			stream.writeInt(color);
			stream.writeDouble(alpha);
			stream.writeInt(zIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void readFrom(DataInputStream stream) {
		try {
			
			x = stream.readShort();
			y = stream.readShort();
			width = stream.readShort();
			height = stream.readShort();
			color = stream.readInt();
			alpha = stream.readDouble();
			zIndex = stream.readInt();
			
		} catch (IOException e) {
			
		}
	}
	
	public void setX(int x2) {
		x = x2;
	}
	
	public void setY(int y2) {
		y = y2;
	}
	
	public void setWidth(int w) {
		width = w;
	}
	
	public void setHeight(int h) {
		height = h;
	}
	
	public void setColor(int c) {
		color = c;
	}
	
	public void setAlpha(double a) {
		alpha = a;
	}

	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public double getAlpha() {
		return alpha;
	}
	
	public int getColor() {
		return color;
	}

}

package openperipheral.common.terminal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import openperipheral.common.tileentity.TileEntityGlassesBridge;
import openperipheral.common.util.ByteUtils;

import org.lwjgl.opengl.GL11;

public class DrawableBox extends BaseDrawable implements IDrawable {

	private short x;
	private short y;
	private short width;
	private short height;
	private int color;
	private double alpha;
	private int color2;
	private double alpha2;
	private byte gradient = 0;
	
	public static final int X_CHANGED 		= 1;
	public static final int Y_CHANGED 		= 2;
	public static final int WIDTH_CHANGED 	= 3;
	public static final int HEIGHT_CHANGED 	= 4;
	public static final int COLOR_CHANGED 	= 5;
	public static final int ALPHA_CHANGED 	= 6;
	public static final int COLOR2_CHANGED 	= 7;
	public static final int ALPHA2_CHANGED 	= 8;
	public static final int Z_CHANGED 		= 9;
	public static final int GRADIENT_CHANGED = 10;

	public DrawableBox() {
		super();
	}

	public DrawableBox(TileEntityGlassesBridge parent, int x, int y, int width,
			int height, int color, double alpha, int color2, double alpha2, byte gradient) {
		super(parent);
		this.x = (short)x;
		this.y = (short)y;
		this.width = (short)width;
		this.height = (short)height;
		this.color = color;
		this.alpha = alpha;
		this.color2 = color2;
		this.alpha2 = alpha2;
		this.gradient = gradient;
		this.methodNames = new String[] { "getX", "setX", "getY", "setY",
				"getWidth", "setWidth", "getHeight", "setHeight", "getColor",
				"setColor", "getAlpha", "setAlpha", "getColor2", "setColor2",
				"getAlpha2", "setAlpha2", "setZIndex", "getZIndex", "setGradient",
				"getGradient", "delete" };
	}

	@Override
	public void draw(ItemStack stack, EntityPlayer player, float partialTicks, boolean hasScreen,
			int mouseX, int mouseY) {
		float r = (float) ((color >> 16) & 0xFF) / 255;
		float g = (float) ((color >> 8) & 0xFF) / 255;
		float b = (float) (color & 0xFF) / 255;

		float r2 = (float) ((color2 >> 16) & 0xFF) / 255;
		float g2 = (float) ((color2 >> 8) & 0xFF) / 255;
		float b2 = (float) (color2 & 0xFF) / 255;
		
		if (gradient == 0) {
			r2 = r;
			g2 = g;
			b2 = b;
		}
		
		Tessellator tessellator = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
		tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(r, g, b, (float)alpha);
        if (gradient == 1) {
			tessellator.addVertex((double) x, (double) y + height, 0.0D);
			tessellator.addVertex((double) x + width, (double) y + height, 0.0D);
        }else {
			tessellator.addVertex((double) x + width, (double) y + height, 0.0D);
			tessellator.addVertex((double) x + width, y, 0.0D);
        	
        }
        tessellator.setColorRGBA_F(r2, g2, b2, (float)alpha2);
        if (gradient == 1) {
			tessellator.addVertex((double) x + width, y, 0.0D);
			tessellator.addVertex((double) x, (double) y, 0.0D);
		}else {
			tessellator.addVertex((double) x, (double) y, 0.0D);
			tessellator.addVertex((double) x, (double) y + height, 0.0D);
		}
		tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
	}

	public double getAlpha() {
		return alpha;
	}

	public double getAlpha2() {
		return alpha2;
	}

	public int getColor() {
		return color;
	}

	public byte getGradient() {
		return gradient;
	}

	public int getColor2() {
		return color2;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public int getZIndex() {
		return zIndex;
	}

	@Override
	public void readFrom(DataInputStream stream, Short changeMask) {
		try {
			
			if (ByteUtils.get(changeMask, X_CHANGED))
				x = stream.readShort();

			if (ByteUtils.get(changeMask, Y_CHANGED))
				y = stream.readShort();

			if (ByteUtils.get(changeMask, WIDTH_CHANGED))
				width = stream.readShort();

			if (ByteUtils.get(changeMask, HEIGHT_CHANGED))
				height = stream.readShort();

			if (ByteUtils.get(changeMask, COLOR_CHANGED))
				color = stream.readInt();
			
			if (ByteUtils.get(changeMask, ALPHA_CHANGED))
				alpha = (double)stream.readFloat();
			
			if (ByteUtils.get(changeMask, COLOR2_CHANGED))
				color2 = stream.readInt();

			if (ByteUtils.get(changeMask, ALPHA2_CHANGED))
				alpha2 = (double)stream.readFloat();
			
			if (ByteUtils.get(changeMask, Z_CHANGED))
				zIndex = stream.readByte();
			
			if (ByteUtils.get(changeMask, GRADIENT_CHANGED))
				gradient = stream.readByte();

		} catch (IOException e) {

		}
	}

	public int setAlpha(double a) {
		if (a == alpha) {
			return -1;
		}
		alpha = a;
		return ALPHA_CHANGED;
	}
	
	public int setAlpha2(double a2) {
		if (alpha2 == a2) {
			return -1;
		}
		alpha2 = a2;
		return ALPHA2_CHANGED;
	}
	
	public int setColor(int c) {
		if (color == c) {
			return -1;
		}
		color = c;
		return COLOR_CHANGED;
	}

	public int setColor2(int c2) {
		if (c2 == color2) {
			return -1;
		}
		color2 = c2;
		return COLOR2_CHANGED;
	}

	public int setHeight(short h) {
		if (height == h) {
			return -1;
		}
		height = h;
		return HEIGHT_CHANGED;
	}

	public int setWidth(short w) {
		if (width == w) {
			return -1;
		}
		width = w;
		return WIDTH_CHANGED;
	}

	public int setGradient(byte g) {
		if (gradient == g) {
			return -1;
		}
		gradient = g;
		return GRADIENT_CHANGED;
	}
	
	public int setX(short x2) {
		if (x == x2) {
			return -1;
		}
		x = x2;
		return X_CHANGED;
	}

	public int setY(short y2) {
		if (y == y2) {
			return -1;
		}
		y = y2;
		return Y_CHANGED;
	}

	public int setZIndex(byte z) {
		if (z == zIndex) {
			return -1;
		}
		zIndex = z;
		return Z_CHANGED;
	}

	@Override
	public void writeTo(DataOutputStream stream, Short changeMask) {
		try {

			if (ByteUtils.get(changeMask, X_CHANGED))
				stream.writeShort((short) x);

			if (ByteUtils.get(changeMask, Y_CHANGED))
				stream.writeShort((short) y);

			if (ByteUtils.get(changeMask, WIDTH_CHANGED))
				stream.writeShort((short) width);
			
			if (ByteUtils.get(changeMask, HEIGHT_CHANGED))
				stream.writeShort((short) height);
			
			if (ByteUtils.get(changeMask, COLOR_CHANGED))
				stream.writeInt(color);
			
			if (ByteUtils.get(changeMask, ALPHA_CHANGED))
				stream.writeFloat((float)alpha);

			if (ByteUtils.get(changeMask, COLOR2_CHANGED))
				stream.writeInt(color2);
			
			if (ByteUtils.get(changeMask, ALPHA2_CHANGED))
				stream.writeFloat((float)alpha2);

			if (ByteUtils.get(changeMask, Z_CHANGED))
				stream.writeByte(zIndex);
			
			if (ByteUtils.get(changeMask, GRADIENT_CHANGED))
				stream.writeByte(gradient);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
